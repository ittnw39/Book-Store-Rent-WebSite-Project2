package io.elice.shoppingmall.user.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.elice.shoppingmall.user.service.CustomOAuth2User;
import io.elice.shoppingmall.user.security.JwtUtil;
import io.elice.shoppingmall.user.service.OAuth2UserServiceImplement;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class OAuthController {

    private static final Logger logger = LoggerFactory.getLogger(OAuthController.class);
    private final OAuth2UserServiceImplement oAuth2UserService;
    private final JwtUtil jwtUtil;
    private final Map<String, String> stateMap = new ConcurrentHashMap<>();

    @GetMapping("/oauth2/authorization/naver")
    public void handleOAuth2AuthorizationRequest(HttpServletResponse response) throws Exception {
        String authorizationUrl = createNaverAuthorizationUrl();
        response.sendRedirect(authorizationUrl);
    }


    private String createNaverAuthorizationUrl() throws Exception {
        String state = generateStateToken();
        String redirectUri = URLEncoder.encode("http://localhost:8080/oauth2/callback/naver",
            "UTF-8");
        String clientId = URLEncoder.encode("uXxBgR5EW1hJ6X7SISYf", "UTF-8");

        stateMap.put(state, state);

        String authorizationUrl = "https://nid.naver.com/oauth2.0/authorize"
            + "?response_type=code"
            + "&client_id=" + clientId
            + "&redirect_uri=" + redirectUri
            + "&state=" + state;

        return authorizationUrl;
    }

    private String generateStateToken() {
        return UUID.randomUUID().toString();
    }
}

//    @PostMapping("/oauth2/callback/naver")
//    public String handleNaverCallback(@RequestParam String state, @RequestParam String code,
//        HttpServletRequest request, HttpServletResponse response) {
//        try {
//            logger.debug("Callback received with code: {} and state: {}", state, code);
//            // state 토큰 검증
//            String savedState = stateMap.get(state);
//            if (savedState == null) {
//                logger.debug("Invalid state token: {}", state);
//                throw new IllegalStateException("Invalid state token");
//            }
//            stateMap.remove(state); // state 토큰 제거
//
//            // 네이버로부터 액세스 토큰 발급받기
//            String accessToken = getNaverAccessToken(state, code);
//            logger.debug("Access Token received: {}", accessToken);
//
//            // 액세스 토큰으로 네이버 사용자 정보 가져오기
//            Map<String, Object> userInfo = getUserInfoFromNaver(accessToken);
//            logger.debug("User info received: {}", userInfo);
//
//            // 사용자 정보 처리 및 인증
//            CustomOAuth2User oAuth2User = oAuth2UserService.processNaverUser(userInfo);
//
//            // JWT 토큰 발급
//            String token = jwtUtil.createToken(new UsernamePasswordAuthenticationToken(oAuth2User, null, oAuth2User.getAuthorities()));
//            logger.debug("JWT Token created: {}", token);
//
//            // 토큰을 쿠키에 저장하여 프론트엔드로 전달
//            Cookie cookie = new Cookie("token", token);
//            cookie.setPath("/");
//            cookie.setHttpOnly(true);
//            response.addCookie(cookie);
//
//            return "redirect:/"; // 메인 페이지로 리다이렉트
//        } catch (Exception e) {
//            logger.error("Error during OAuth2 callback handling", e);
//            return "redirect:/login/login.html"; // 에러 발생 시 에러 페이지로 리다이렉트
//        }
//    }

//    private String getNaverAccessToken(String code, String state) throws Exception {
//        String redirectUri = URLEncoder.encode("http://localhost:8080/oauth2/callback/naver", "UTF-8");
//
//        String requestUrl = "https://nid.naver.com/oauth2.0/token";
//        String requestData = "grant_type=authorization_code"
//            + "&client_id=" + URLEncoder.encode("uXxBgR5EW1hJ6X7SISYf", "UTF-8")
//            + "&client_secret=" + URLEncoder.encode("KUTaz7Gysg", "UTF-8")
////            + "&redirect_uri=" + redirectUri
//            + "&state=" + state
//            + "&code=" + code;
//
//        HttpURLConnection con = (HttpURLConnection) new URL(requestUrl).openConnection();
//        con.setRequestMethod("GET");
//        con.setDoOutput(true);
//
//        try (OutputStream out = con.getOutputStream()) {
//            out.write(requestData.getBytes());
//            out.flush();
//        }
//
//        int responseCode = con.getResponseCode();
//        BufferedReader br = responseCode == 200
//            ? new BufferedReader(new InputStreamReader(con.getInputStream()))
//            : new BufferedReader(new InputStreamReader(con.getErrorStream()));
//
//        StringBuilder res = new StringBuilder();
//        String inputLine;
//        while ((inputLine = br.readLine()) != null) {
//            res.append(inputLine);
//        }
//        br.close();
//
//        String responseBody = res.toString();
//        String accessToken = extractAccessToken(responseBody);
//
//        return accessToken;
//    }
//
//    private Map<String, Object> getUserInfoFromNaver(String accessToken) throws Exception {
//        String apiURL = "https://openapi.naver.com/v1/nid/me";
//
//        HttpURLConnection con = (HttpURLConnection) new URL(apiURL).openConnection();
//        con.setRequestMethod("GET");
//        con.setRequestProperty("Authorization", "Bearer " + accessToken);
//
//        int responseCode = con.getResponseCode();
//        BufferedReader br = responseCode == 200
//            ? new BufferedReader(new InputStreamReader(con.getInputStream()))
//            : new BufferedReader(new InputStreamReader(con.getErrorStream()));
//
//        StringBuilder res = new StringBuilder();
//        String inputLine;
//        while ((inputLine = br.readLine()) != null) {
//            res.append(inputLine);
//        }
//        br.close();
//
//        String responseBody = res.toString();
//        Map<String, Object> userInfo = parseUserInfo(responseBody);
//
//        return userInfo;
//    }
//
//    private String extractAccessToken(String response) {
//        JsonObject json = JsonParser.parseString(response).getAsJsonObject();
//        return json.get("access_token").getAsString();
//    }
//
//    private Map<String, Object> parseUserInfo(String response) {
//        JsonObject json = JsonParser.parseString(response).getAsJsonObject();
//        JsonObject responseObj = json.getAsJsonObject("response");
//
//        String email = responseObj.get("email").getAsString();
//
//        Map<String, Object> userInfo = new HashMap<>();
//        userInfo.put("email", email);
//        return userInfo;
//    }
//

//}
