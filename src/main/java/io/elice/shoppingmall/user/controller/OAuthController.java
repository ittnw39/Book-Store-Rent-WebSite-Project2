package io.elice.shoppingmall.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.elice.shoppingmall.user.dto.UserDTO;
import io.elice.shoppingmall.user.entity.User;
import io.elice.shoppingmall.user.security.JwtUtil;
import io.elice.shoppingmall.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;



@Controller
@RequiredArgsConstructor
public class OAuthController {


    private final OAuth2AuthorizedClientService authorizedClientService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

//    @GetMapping("/api/v1/auth/oauth2/naver")
//    public ResponseEntity<String> handleOAuth2AuthorizationRequest(HttpServletRequest request) {
//        String authorizationUrl = createNaverAuthorizationUrl(request);
//        return ResponseEntity.ok(authorizationUrl);
//    }
//
//
//    private String createNaverAuthorizationUrl(HttpServletRequest request) {
//        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient("naver", request.getSession().getId());
//
//        if (authorizedClient == null) {
//            return "redirect:/oauth2/authorization/naver";
//        }
//
//        String authorizationUrl = authorizedClient.getClientRegistration().getProviderDetails().getAuthorizationUri();
//        String state = UUID.randomUUID().toString();
//
//        return UriComponentsBuilder.fromUriString(authorizationUrl)
//            .queryParam("response_type", "code")
//            .queryParam("client_id", authorizedClient.getClientRegistration().getClientId())
//            .queryParam("redirect_uri", authorizedClient.getClientRegistration().getRedirectUri())
//            .queryParam("state", state)
//            .build()
//            .toUriString();
//    }

    @GetMapping("/users/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("email", email);
            response.put("roles", roles);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

//    @GetMapping("/oauth2/callback/naver")
//    public String handleNaverCallback(@RequestParam String code, @RequestParam String state, HttpServletRequest request, HttpServletResponse response) {
//        // 네이버 로그인 콜백 처리
//        OAuth2AuthenticationToken authentication = (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
//        OAuth2User oAuth2User = authentication.getPrincipal();
//        String email = oAuth2User.getAttribute("email");
//
//        // 사용자 정보 조회 또는 생성
//        User user = userService.findUserByEmail(email);
//        if (user == null) {
//            user = new User();
//            user.setEmail(email);
//            user.setAuthProvider("naver");
//            userService.createUser(new UserDTO(user));
//        }
//
//        // 역할 정보 추가
//        List<String> roles = user.getAuthorities().stream()
//            .map(GrantedAuthority::getAuthority)
//            .collect(Collectors.toList());
//
//        // 응답 데이터 생성
//        Map<String, Object> responseData = new HashMap<>();
//        responseData.put("roles", roles);
//
//        // 응답 데이터를 JSON 형식으로 변환하여 응답
//        response.setContentType("application/json");
//        response.setCharacterEncoding("UTF-8");
//        response.getWriter().write(new ObjectMapper().writeValueAsString(responseData));
//
//        return null;
//    }

//    @GetMapping("/oauth2/callback/naver")
//        public String handleNaverCallback(@RequestParam String code, @RequestParam String state, HttpSession session) {
//            // 네이버 로그인 콜백 처리
//            OAuth2AuthenticationToken authentication = (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
//            OAuth2User oAuth2User = authentication.getPrincipal();
//            String email = oAuth2User.getAttribute("email");
//
//            // 사용자 정보 조회 또는 생성
//            User user = userService.findUserByEmail(email);
//            if (user == null) {
//                user = new User();
//                user.setEmail(email);
//                user.setAuthProvider("naver");
//                userService.createUser(new UserDTO(user));
//            }
//
//            // 세션에 사용자 정보 저장
//            session.setAttribute("user", user);
//
//            return "redirect:/";
//        }
    }





