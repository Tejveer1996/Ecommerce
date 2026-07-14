package dev.Tejveer.EcomUserAuthService.Config;

import dev.Tejveer.EcomUserAuthService.DTO.TokenDetails;
import dev.Tejveer.EcomUserAuthService.Entity.Roles;
import dev.Tejveer.EcomUserAuthService.Entity.User;
import dev.Tejveer.EcomUserAuthService.Repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Email is used as username
 */

@Component
public class JwtUtils {

    @Value("${auth.secret.key}")
    private String SECRET_KEY;

    @Value("${auth.access.token.expiry}")
    private Long ACCESS_TOKEN_EXPIRY;

    @Value("${auth.refresh.token.expiry}")
    private Long REFRESH_TOKEN_EXPIRY;

    @Autowired
    UserRepository userRepository;

    public TokenDetails generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("roles", user.getRoles().stream().map(Roles::name).collect(Collectors.toList()));
        return createToken(claims, user.getId().toString());
    }

    public TokenDetails generateRefreshToken(User user) {
        String token = Jwts.builder()
                .subject(user.getId().toString())
                .signWith(getSignKey())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRY))
                .issuedAt(new Date(System.currentTimeMillis()))
                .compact();
        return TokenDetails.builder()
                .token(token)
                .expiry(REFRESH_TOKEN_EXPIRY)
                .build();
    }

    private TokenDetails createToken(Map<String, Object> claims, String subject) {
        String token = Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRY))
                .signWith(getSignKey())
                .compact();
        return TokenDetails.builder()
                .token(token)
                .expiry(ACCESS_TOKEN_EXPIRY)
                .build();
    }


    private Key getSignKey() {
        byte[] bytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(bytes);
    }

    /**
     * This method validate the refresh token and return the userId present in the subject
     * which will be used to generate access token after refresh token validation.
     *
     * @param refreshToken
     * @return
     */
    public String validateRefreshToken(String refreshToken) {
        Claims claims = Jwts.parser()
                .verifyWith((SecretKey) getSignKey())
                .build()
                .parseSignedClaims(refreshToken)
                .getPayload();
        UUID userId = UUID.fromString(claims.getSubject());
        if (isTokenExpired(claims.getExpiration()) && userRepository.existsById(userId)) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        return claims.getSubject();
    }

    //
//    public boolean validateToken(String token, UserDetails userDetails) {
//        String userName = extractUserName(token);
//        return (userName.equals(userDetails.getUsername())) && isTokenExpired(token);
//    }
//
    private boolean isTokenExpired(Date expiryDate) {
        return expiryDate.before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    public String extractUserId(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimResolver) {
        Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    /**
     * This token will verify and extract the claims
     *
     * @param token
     * @return
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
