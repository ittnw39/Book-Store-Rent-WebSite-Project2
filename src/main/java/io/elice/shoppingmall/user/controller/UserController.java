package io.elice.shoppingmall.user.controller;

import io.elice.shoppingmall.user.dto.UserDTO;
import io.elice.shoppingmall.user.security.JwtUtil;
import io.elice.shoppingmall.user.service.JwtBlacklistService;
import io.elice.shoppingmall.user.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final JwtBlacklistService blacklistService;

//    public UserController(UserService userService, JwtUtil jwtUtil, JwtBlacklistService blacklistService) {
//        this.userService = userService;
//        this.jwtUtil = jwtUtil;
//        this.blacklistService = blacklistService;
//    }


    @GetMapping("/login")
    public String loginPage() {
        return "login/login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register/register";
    }


    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserDTO userDTO) {
        if (userService.isEmailDuplicate(userDTO.getEmail())) {
            return ResponseEntity.badRequest().body("이미 존재하는 이메일입니다.");
        }

        try {
            userService.createUser(userDTO);
            return ResponseEntity.ok("회원 가입이 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> userData) {
        String email = userData.get("email");
        String password = userData.get("password");

        try {
            String token = userService.login(email, password);
            return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body("로그인 성공");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwt = authorizationHeader.substring(7);

            // JwtUtil의 메서드 사용
            Claims claims = jwtUtil.validateToken(jwt);
            if (claims != null) {
                Date expirationDate = claims.getExpiration();

                // 블랙리스트에 추가
                blacklistService.blacklist(jwt, expirationDate);
            }
        }

        // 세션 무효화
        request.getSession().invalidate();

        // SecurityContextHolder에서 인증 정보 제거
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok("로그아웃 되었습니다.");
    }


    @PutMapping("/mypage/update")
    public ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO userDTO, @RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring(7);
            String email = jwtUtil.getEmailFromToken(jwt);
            if (email != null) {
                UserDTO updatedUser = userService.updateUser(email, userDTO);
                return ResponseEntity.ok(updatedUser);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @DeleteMapping("/mypage/delete")
    public ResponseEntity<String> deleteUser(@RequestHeader("Authorization") String token) {
        String email = jwtUtil.getEmailFromToken(token.substring(7)); // "Bearer " 제거
        userService.deleteUser(email);
        return ResponseEntity.ok("회원탈퇴가 완료되었습니다.");
    }

}

