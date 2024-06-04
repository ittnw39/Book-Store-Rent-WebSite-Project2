package io.elice.shoppingmall.user.service;



import io.elice.shoppingmall.user.security.JwtUtil;
import io.elice.shoppingmall.user.dto.UserDTO;
import io.elice.shoppingmall.user.entity.User;
import io.elice.shoppingmall.user.repository.UserRepository;
import io.elice.shoppingmall.user.security.JwtUtil;
import java.util.Collections;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository,
        AuthenticationManager authenticationManager, BCryptPasswordEncoder passwordEncoder,
        JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public boolean isEmailDuplicate(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public void createUser(UserDTO userDTO) {
        validatePassword(userDTO.getPassword(), userDTO.getPasswordConfirm());

        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setPhNum(userDTO.getPhNum());
        user.setAddress(userDTO.getAddress());
        user.setNickname(userDTO.getNickname());

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
                () -> new UsernameNotFoundException("User not found with email: " + email));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
            .username(user.getEmail())
            .password(user.getPassword())
            .authorities(Collections.emptyList())
            .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtUtil.createToken(authentication);
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

        userRepository.delete(user);
    }
}
