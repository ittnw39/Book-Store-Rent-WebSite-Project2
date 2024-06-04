package io.elice.shoppingmall.user.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {
    // Secret key for signing JWT
    private static final SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(
        "jwtpassword123jwtpassword123jwtpassword123jwtpassword123jwtpassword"
    ));

    // Expiration time in milliseconds (e.g., 10 hours)
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 10;


    // Create JWT token
    public String createToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return Jwts.builder()
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .signWith(SignatureAlgorithm.HS512, key)
            .compact();
    }

    // Validate JWT token
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            System.out.println("Invalid JWT token: " + e.getMessage());
            return false;
        }
    }

    // Extract email from JWT token
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }
}
