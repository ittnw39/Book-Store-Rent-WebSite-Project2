package io.elice.shoppingmall.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User, UserDetails {

    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;



    @Override
    public Map<String, Object> getAttributes() {
        return Collections.singletonMap("email", email);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null; // 소셜 로그인의 경우 비밀번호가 없으므로 null을 반환합니다.
    }

    @Override
    public String getUsername() {
        return email;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getName() {
        return email; // 이메일을 이름으로 사용합니다.
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}