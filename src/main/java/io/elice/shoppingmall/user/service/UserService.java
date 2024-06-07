package io.elice.shoppingmall.user.service;



import io.elice.shoppingmall.user.security.JwtUtil;
import io.elice.shoppingmall.user.dto.UserDTO;
import io.elice.shoppingmall.user.entity.User;
import io.elice.shoppingmall.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final JwtBlacklistService blacklistService;


    public boolean isEmailDuplicate(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Transactional
    public void createAdminUser(String email, String password) {
        User adminUser = new User();
        adminUser.setEmail(email);
        adminUser.setPassword(passwordEncoder.encode(password));
        adminUser.setAdmin(true);
        userRepository.save(adminUser);
    }

    public void createUser(UserDTO userDTO) {
        if (isEmailDuplicate(userDTO.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        validatePassword(userDTO.getPassword(), userDTO.getPasswordConfirm());

        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setPhNum(userDTO.getPhNum());
        user.setAddress(userDTO.getAddress());
        user.setNickname(userDTO.getNickname());
        user.setAdmin(userDTO.isAdmin());

        userRepository.save(user);
    }


    private void validatePassword(String password, String passwordConfirm) {
        if (!password.equals(passwordConfirm)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(
                () -> new UsernameNotFoundException("가입되지 않은 이메일이거나 회원 탈퇴로 인해 로그인할 수 없습니다."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        if (user.isAdmin()) {
            authorities.add(new SimpleGrantedAuthority("ADMIN"));
        } else {
            authorities.add(new SimpleGrantedAuthority("USER"));
        }

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
            .username(user.getEmail())
            .password(user.getPassword())
            .authorities(authorities)
            .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtUtil.createToken(authentication);
        return token;
    }

//    public boolean isAdmin(String email) {
//        User user = userRepository.findByEmail(email)
//            .orElseThrow(
//                () -> new UsernameNotFoundException("가입되지 않은 이메일이거나 회원 탈퇴로 인해 로그인할 수 없습니다."));
//         return user.isAdmin();
//    }


    public String logout(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring(7);

            // JwtUtil의 메서드 사용
            Claims claims = jwtUtil.validateToken(jwt);
            if (claims != null) {
                String email = claims.getSubject();
                String logoutToken = jwtUtil.createLogoutToken(email); // 만료 시간이 0초인 토큰 발급
            }
        }

        // SecurityContextHolder에서 인증 정보 제거
        SecurityContextHolder.clearContext();

        return "로그아웃 되었습니다.";
    }


    @Transactional
    public UserDTO updateUser(String email, UserDTO userDTO) {
        if (email == null) {
            throw new IllegalArgumentException("Invalid token");
        }
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        user.setUsername(userDTO.getUsername());
        user.setPhNum(userDTO.getPhNum());
        user.setAddress(userDTO.getAddress());
        user.setNickname(userDTO.getNickname());

        return new UserDTO(user);
    }

    @Transactional
    public void deleteUser(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        blacklistService.blacklistUserId(user.getId()); // userId를 블랙리스트에 추가
        userRepository.delete(user);
    }
}
