package io.elice.shoppingmall.user;

import io.elice.shoppingmall.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureException;
import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    static final SecretKey key =
        Keys.hmacShaKeyFor(Decoders.BASE64.decode(
            "jwtpassword123jwtpassword123jwtpassword123jwtpassword123jwtpassword"
        ));

    // JWT 만들어주는 함수
    public static String createToken(Authentication auth) {
        var user = (User) auth.getPrincipal();
        var authorities = auth.getAuthorities().stream()
            .map(a -> a.getAuthority()).collect(Collectors.joining(","));
        String jwt = Jwts.builder()
            .claim("userid", user.getLevel())
            .claim("username", user.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + 10000)) // 유효기간 10초
            .signWith(key)
            .compact();
        return jwt;
    }

    // JWT 까주는 함수
    public static Claims extractToken(String token, Key key) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public boolean validateToken(String token, Key key) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SignatureException | MalformedJwtException | ExpiredJwtException |
                 UnsupportedJwtException | IllegalArgumentException e) {
            // 로그 남기기
            System.out.println("Invalid JWT token: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public String getEmailFromToken(String token, Key key) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return claims.getSubject(); // 일반적으로 이메일은 subject 클레임에 저장됩니다.
    }
}
