package io.elice.shoppingmall.user.controller;

import io.elice.shoppingmall.user.dto.UserDTO;
import io.elice.shoppingmall.user.security.JwtUtil;
import io.elice.shoppingmall.user.service.CustomUserDetailsService;
import io.elice.shoppingmall.user.service.JwtBlacklistService;
import io.elice.shoppingmall.user.service.UserService;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final CustomUserDetailsService customUserDetailsService;
    private final UserService userService;
    private final JwtUtil jwtUtil;




    @GetMapping("/login")
    public String loginPage() {
        return "/login/login.html"; // 정적 파일로 리디렉션
    }



    @GetMapping("/register")
    public String registerPage() {
        return "/register/register.html";
    }



    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
        try {
            userService.createUser(userDTO);
            return ResponseEntity.ok(Collections.singletonMap("message", "회원 가입이 완료되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Collections.singletonMap("reason", e.getMessage()));
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> userData) {
        String email = userData.get("email");
        String password = userData.get("password");

        try {
            String token = userService.login(email, password);
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            // 사용자의 역할 정보 가져오기
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
            List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "로그인되었습니다.");
            response.put("token", token);
            response.put("roles", roles);

            return ResponseEntity.ok()
                .headers(headers)
                .body(response);
        } catch (UsernameNotFoundException | IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "잘못된 이메일 또는 비밀번호입니다.");
            return ResponseEntity.badRequest().body(error);
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        String message = userService.logout(token);
        return ResponseEntity.ok(message);
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

