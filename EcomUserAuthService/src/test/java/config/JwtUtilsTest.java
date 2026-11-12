package config;

import dev.Tejveer.EcomUserAuthService.Config.JwtUtils;
import dev.Tejveer.EcomUserAuthService.DTO.TokenDetails;
import dev.Tejveer.EcomUserAuthService.Entity.Roles;
import dev.Tejveer.EcomUserAuthService.Entity.User;
import dev.Tejveer.EcomUserAuthService.Repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link JwtUtils}: the class that creates and checks JWTs for the User Auth service.
 *
 * What is tested:
 *  - Access tokens carry the right user id, email and roles.
 *  - Tokens signed with a different key, or edited after signing (e.g. USER changed to ADMIN), are rejected.
 *  - Expired access and refresh tokens are rejected.
 *  - A valid refresh token returns the user's id; one for a user who no longer exists is rejected.
 *  - An access token is not accepted as a refresh token (currently fails: known security gap,
 *    because both token types use the same key and nothing marks which type a token is).
 *
 * How it works:
 *  - Plain unit test: no Spring context, no database, no Docker. Runs in milliseconds.
 *  - A throwaway RSA key pair is generated for each test run, so real keys are never used.
 *  - UserRepository is a Mockito mock, so we decide what it returns.
 *  - JwtUtils normally gets its keys and expiry times from @Value; here they are set with
 *    ReflectionTestUtils because Spring is not running.
 */

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {

    private static final long ONE_HOUR = 60 * 60 * 1000L;

    // Throwaway RSA keys made just for the tests (never your real keys)
    private static KeyPair testKeys;
    private static KeyPair otherKeys;   // a "wrong" key pair, to fake an attacker

    @Mock
    private UserRepository userRepository;   // fake repository, we decide what it returns

    private JwtUtils jwtUtils;
    private User user;

    @BeforeAll
    static void createTestKeys() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        testKeys = generator.generateKeyPair();
        otherKeys = generator.generateKeyPair();
    }

    @BeforeEach
    void setUp() {
        jwtUtils = jwtUtilsWith(testKeys, ONE_HOUR, ONE_HOUR);
        user = User.builder()
                .id(UUID.randomUUID())
                .name("Test User")
                .email("test.user@example.com")
                .password("not-used-here")
                .roles(List.of(Roles.USER))
                .build();
    }

    /** Creates a JwtUtils with the given keys and expiry times (Spring's @Value is not running here). */
    private JwtUtils jwtUtilsWith(KeyPair keys, long accessExpiryMs, long refreshExpiryMs) {
        JwtUtils utils = new JwtUtils();
        ReflectionTestUtils.setField(utils, "privateKey",
                Base64.getEncoder().encodeToString(keys.getPrivate().getEncoded()));
        ReflectionTestUtils.setField(utils, "publicKey",
                Base64.getEncoder().encodeToString(keys.getPublic().getEncoded()));
        ReflectionTestUtils.setField(utils, "ACCESS_TOKEN_EXPIRY", accessExpiryMs);
        ReflectionTestUtils.setField(utils, "REFRESH_TOKEN_EXPIRY", refreshExpiryMs);
        ReflectionTestUtils.setField(utils, "userRepository", userRepository);
        return utils;
    }

    // ---------------------------------------------------------------- access token

    @Test
    @DisplayName("Access token carries the user's id, email and roles")
    void accessToken_containsUserIdEmailAndRoles() throws Exception {
        // when
        TokenDetails token = jwtUtils.generateToken(user);

        // then
        Claims claims = jwtUtils.extractAllClaims(token.getToken());
        assertThat(claims.getSubject()).isEqualTo(user.getId().toString());
        assertThat(claims.get("email", String.class)).isEqualTo("test.user@example.com");
        assertThat(claims.get("roles", List.class)).containsExactly("USER");
        assertThat(token.getExpiry()).isEqualTo(ONE_HOUR);
    }

    @Test
    @DisplayName("A token signed with someone else's key is rejected")
    void tokenSignedWithAnotherKey_isRejected() throws Exception {
        // given: an attacker creates a token with their own private key
        JwtUtils attacker = jwtUtilsWith(otherKeys, ONE_HOUR, ONE_HOUR);
        String forgedToken = attacker.generateToken(user).getToken();

        // then: our public key does not accept it
        assertThatThrownBy(() -> jwtUtils.extractAllClaims(forgedToken))
                .isInstanceOf(JwtException.class);
    }

    @Test
    @DisplayName("Changing the token's content (USER -> ADMIN) breaks the signature")
    void tamperedToken_isRejected() throws Exception {
        // given: a real token, with its middle part (the claims) edited to say ADMIN
        String[] parts = jwtUtils.generateToken(user).getToken().split("\\.");
        String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        String hackedPayload = payload.replace("\"USER\"", "\"ADMIN\"");
        String hackedToken = parts[0] + "."
                + Base64.getUrlEncoder().withoutPadding()
                .encodeToString(hackedPayload.getBytes(StandardCharsets.UTF_8))
                + "." + parts[2];

        assertThat(hackedPayload).contains("ADMIN");   // make sure we really changed it

        // then: the signature no longer matches
        assertThatThrownBy(() -> jwtUtils.extractAllClaims(hackedToken))
                .isInstanceOf(JwtException.class);
    }

    @Test
    @DisplayName("An expired access token is rejected")
    void expiredAccessToken_isRejected() throws Exception {
        // given: a token that expired 1 second ago
        JwtUtils expiring = jwtUtilsWith(testKeys, -1000L, ONE_HOUR);
        String token = expiring.generateToken(user).getToken();

        assertThatThrownBy(() -> jwtUtils.extractAllClaims(token))
                .isInstanceOf(ExpiredJwtException.class);
    }

    // ---------------------------------------------------------------- refresh token

    @Test
    @DisplayName("A valid refresh token returns the user's id")
    void validRefreshToken_returnsUserId() throws Exception {
        when(userRepository.existsById(user.getId())).thenReturn(true);
        String refreshToken = jwtUtils.generateRefreshToken(user).getToken();

        String userId = jwtUtils.validateRefreshToken(refreshToken);

        assertThat(userId).isEqualTo(user.getId().toString());
    }

    @Test
    @DisplayName("A refresh token for a user who no longer exists is rejected")
    void refreshToken_forDeletedUser_isRejected() throws Exception {
        when(userRepository.existsById(user.getId())).thenReturn(false);
        String refreshToken = jwtUtils.generateRefreshToken(user).getToken();

        assertThatThrownBy(() -> jwtUtils.validateRefreshToken(refreshToken))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid credentials");
    }

    @Test
    @DisplayName("An expired refresh token is rejected")
    void expiredRefreshToken_isRejected() throws Exception {
        JwtUtils expiring = jwtUtilsWith(testKeys, ONE_HOUR, -1000L);
        String refreshToken = expiring.generateRefreshToken(user).getToken();

        assertThatThrownBy(() -> jwtUtils.validateRefreshToken(refreshToken))
                .isInstanceOf(ExpiredJwtException.class);
    }

    // EXPECTED TO FAIL with the current code: it shows a real security gap.
    // Access and refresh tokens are signed with the same key, and nothing marks which is which,
    // so the refresh endpoint also accepts an access token.
    @Test
    @DisplayName("An access token must NOT be accepted as a refresh token")
    void accessToken_isNotAcceptedAsRefreshToken() throws Exception {
        lenient().when(userRepository.existsById(user.getId())).thenReturn(true);
        String accessToken = jwtUtils.generateRefreshToken(user).getToken();

        assertThatThrownBy(() -> jwtUtils.validateRefreshToken(accessToken))
                .isInstanceOf(Exception.class);
    }
}
