package io.elice.shoppingmall.user.controller;


import io.elice.shoppingmall.user.dto.UserDTO;
import io.elice.shoppingmall.user.entity.User;
import io.elice.shoppingmall.user.repository.UserRepository;
import io.elice.shoppingmall.user.security.JwtUtil;
import io.elice.shoppingmall.user.service.CustomUserDetailsService;
import io.elice.shoppingmall.user.service.JwtBlacklistService;
import io.elice.shoppingmall.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final CustomUserDetailsService customUserDetailsService;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;


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
    public ResponseEntity<?> login(@RequestBody Map<String, String> userData, HttpServletResponse response) {
        String email = userData.get("email");
        String password = userData.get("password");

        try {
            String token = userService.login(email, password);

            // 쿠키에 토큰 저장
            Cookie cookie = new Cookie("jwtToken", token);
            cookie.setMaxAge(3600); // 쿠키 유효기간 설정 (예: 24시간)
            cookie.setPath("/"); // 쿠키 경로 설정
            response.addCookie(cookie);

            // 사용자의 역할 정보 가져오기
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
            List<String> roles = authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("message", "로그인되었습니다.");
            responseData.put("token", token); // 응답 바디에 토큰 포함
            responseData.put("roles", roles);

            return ResponseEntity.ok(responseData);
        } catch (UsernameNotFoundException | IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "잘못된 이메일 또는 비밀번호입니다.");
            return ResponseEntity.badRequest().body(error);
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token, HttpServletRequest request, HttpServletResponse response) {
        String message = userService.logout(token);

        // 현재 요청의 도메인 가져오기
        String domain = request.getServerName();

        // 쿠키 만료 설정
//        Cookie cookie = new Cookie("jwtToken", null);
//        cookie.setMaxAge(0); // 쿠키 유효기간을 0으로 설정하여 즉시 만료
//        cookie.setPath("/"); // 쿠키 경로 설정
//        cookie.setDomain(domain); // 도메인 설정
//        response.addCookie(cookie);

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


    @GetMapping("/admin-check")
    public ResponseEntity<?> checkAdmin(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring(7);
            String email = jwtUtil.getEmailFromToken(jwt);
            if (email != null) {
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
                Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
                boolean isAdmin = authorities.stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ADMIN"));
                if (isAdmin) {
                    return ResponseEntity.ok(Collections.singletonMap("result", "success"));
                }
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("result", "fail"));
    }


    @GetMapping("/users/all")
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/account")
    public String accountPage() {
        return "/account/account.html";
    }

    @GetMapping("/account/security")
    public String accountSecurityPage() {
        return "/account-security/account-security.html";
    }

    @GetMapping("/account/signout")
    public String signoutPage() {
        return "/account-signout/account-signout.html";
    }


    @PostMapping("/password-check")
    public ResponseEntity<?> checkPassword(@RequestHeader("Authorization") String token,
                                           @RequestBody Map<String, String> passwordData) {
        String email = jwtUtil.getEmailFromToken(token.substring(7));

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }
        User user = optionalUser.get();

        String inputPassword = passwordData.get("password");

        if (passwordEncoder.matches(inputPassword, user.getPassword())) {
            // 비밀번호가 일치하면 사용자의 ID를 포함하는 객체를 반환합니다.
            Map<String, Long> responseBody = new HashMap<>();
            responseBody.put("id", user.getId());
            return ResponseEntity.ok(responseBody);
        } else {
            // 비밀번호가 일치하지 않으면 에러 메시지를 반환합니다.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("잘못된 비밀번호입니다.");
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }

        // 사용자 삭제
        userRepository.deleteById(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "사용자가 성공적으로 삭제되었습니다.");
        return ResponseEntity.ok(response);

    }


    @GetMapping("/data")
    public ResponseEntity<?> getUserData(@RequestHeader("Authorization") String token) {

        String email = jwtUtil.getEmailFromToken(token.substring(7));

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }
        User user = optionalUser.get();

        return ResponseEntity.ok(user);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id,
        @RequestBody Map<String, Object> updates) {

        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        updates.forEach((key, value) -> {
            switch (key) {
                case "username":
                    user.setUsername((String) value);
                    break;
                case "email":
                    user.setEmail((String) value);
                    break;
                case "phNum":
                    user.setPhone_number((String) value);
                    break;
                case "password":
                    String encodedPassword = passwordEncoder.encode((String) value);
                    user.setPassword(encodedPassword);
                    break;
            }
        });
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }





}

