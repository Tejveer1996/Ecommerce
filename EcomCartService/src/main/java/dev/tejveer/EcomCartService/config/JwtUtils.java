package dev.tejveer.EcomCartService.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

/**
 * Email is used as username
 */

@Component
public class JwtUtils {
    @Value("${jwt.public.key}")
    private String publicKey;

    public PublicKey getPublicKey() throws Exception {

        byte[] keyBytes = Base64.getDecoder().decode(publicKey);

        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);

        return KeyFactory.getInstance("RSA")
                .generatePublic(spec);
    }

    private boolean isTokenExpired(Date expiryDate) {
        return expiryDate.before(new Date());
    }

    private Date extractExpiration(String token) throws Exception {
        return extractClaims(token, Claims::getExpiration);
    }

    public String extractUserId(String token) throws Exception {
        return extractClaims(token, Claims::getSubject);
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimResolver) throws Exception {
        Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    /**
     * This token will verify and extract the claims
     *
     * @param token
     * @return
     */
    public Claims extractAllClaims(String token) throws Exception {
        return Jwts.parser()
                .verifyWith(getPublicKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
