package io.elice.shoppingmall.user.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.SecureRandom;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {
    // Secret key for signing JWT
    private static final SecretKey key = Keys.hmacShaKeyFor(generateRandomBytes());

    // Expiration time in milliseconds (e.g., 10 hours) = 10시간
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 10;

    // Generate random bytes for secret key
    private static byte[] generateRandomBytes() {
        byte[] keyBytes = new byte[64]; // 64 bytes = 512 bits
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(keyBytes);
        return keyBytes;
    }



    // Create JWT token
    public String createToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return Jwts.builder()
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .signWith(key, SignatureAlgorithm.HS512)
            .compact();
    }

    public String createLogoutToken(String email) {
        return Jwts.builder()
            .setSubject(email)
            .setIssuedAt(new Date())
            .setExpiration(new Date()) // 만료 시간을 현재 시간으로 설정하여 즉시 만료되도록 함
            .signWith(key, SignatureAlgorithm.HS512)
            .compact();
    }

    // Validate JWT token
    public Claims validateToken(String token) {
        try {
            return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (Exception e) {
            // 토큰 검증 실패 시 예외 처리
            System.out.println("Invalid JWT token: " + e.getMessage());
            return null;
        }
    }

    // Extract email from JWT token
    public String getEmailFromToken(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();
            return claims.getSubject();
        } catch (Exception e) {
            // 토큰 파싱 또는 유효성 검사 실패 시 예외 처리
            return null;
        }
    }
}
