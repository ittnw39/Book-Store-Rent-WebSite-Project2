
package io.elice.shoppingmall.user.security;

import io.elice.shoppingmall.user.entity.CustomOAuth2User;
import io.elice.shoppingmall.user.repository.UserRepository;
import io.elice.shoppingmall.user.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.security.core.GrantedAuthority;
import io.jsonwebtoken.Claims;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    // 하드코딩된 시크릿 키 값
    private static final String SECRET_KEY = "jwtpassword123jwtpassword123jwtpassword123jwtpassword123jwtpassword123jwtpassword123jwtpassword123jwtpassword123jwtpassword123jwtpassword123";

    // 토큰 유효 기간 (예: 10시간)
    private static final long EXPIRATION_TIME = 1000L * 60 * 60 * 10;

    public String createToken(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            username = userDetails.getUsername();
        } else if (principal instanceof CustomOAuth2User) {
            CustomOAuth2User oAuth2User = (CustomOAuth2User) principal;
            username = oAuth2User.getName();
        } else {
            throw new IllegalArgumentException("Invalid principal type");
        }

        // 필수 클레임 설정
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", username); // 주체(subject) 클레임
        claims.put("iat", new Date()); // 발행 시간(issued at) 클레임
        claims.put("exp", new Date(System.currentTimeMillis() + EXPIRATION_TIME)); // 만료 시간(expiration) 클레임
        claims.put("roles", authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList()));

        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

        return Jwts.builder()
            .claims(claims) // 클레임 설정
            .signWith(key, Jwts.SIG.HS512) // 서명 키 설정
            .compact(); // 토큰 문자열 생성
    }

    public String createLogoutToken(String email) {

        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date()) // 만료 시간을 현재 시간으로 설정하여 즉시 만료되도록 함
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }

    // Validate JWT token
    public Claims validateToken(String token) {

        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

        try {
            return Jwts.parser()
                .verifyWith(key)
                .clockSkewSeconds(30)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        } catch (ExpiredJwtException e) {
            // 토큰이 만료된 경우 예외 처리
            System.out.println("Token has expired: " + e.getMessage());
            return null;
        } catch (Exception e) {
            // 토큰 검증 실패 시 예외 처리
            System.out.println("Invalid JWT token: " + e.getMessage());
            return null;
        }
    }

    // Extract email from JWT token
    public String getEmailFromToken(String token) {

        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getSubject();
        } catch (Exception e) {
            // 토큰 파싱 또는 유효성 검사 실패 시 예외 처리
            return null;
        }

    }
}

