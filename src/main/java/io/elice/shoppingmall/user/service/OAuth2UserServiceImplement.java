package io.elice.shoppingmall.user.service;

import io.elice.shoppingmall.user.entity.User;
import io.elice.shoppingmall.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuth2UserServiceImplement extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String oauthClientName = userRequest.getClientRegistration().getClientName();

        return processOAuth2User(oAuth2User, oauthClientName);
    }

    private CustomOAuth2User processOAuth2User(OAuth2User oAuth2User, String oauthClientName) {
        String email = null;
        String userId = null;

        if (oauthClientName.equals("naver")) {
            Map<String, String> responseMap = (Map<String, String>) oAuth2User.getAttributes().get("response");
            userId = "naver_" + responseMap.get("id").substring(0, 14);
            email = responseMap.get("email");
        }

        // 이메일이 유효하고, 데이터베이스에 없는 새로운 사용자라면 사용자 생성
        if (email != null && !email.isBlank() && isValidEmail(email) && !userRepository.existsByEmail(email)) {
            User user = new User();
            user.setEmail(email);
            user.setAuthProvider(oauthClientName);
            user.setAdmin(false);
            user.setPassword("");
            user.setPhone_number("");
            userRepository.save(user);
        }

        // 권한 정보 설정
        Collection<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("USER"));

        return new CustomOAuth2User(email, authorities);
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        return email.matches(emailPattern);
    }
}