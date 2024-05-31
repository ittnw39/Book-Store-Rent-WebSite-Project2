package io.elice.shoppingmall.user.service;

import io.elice.shoppingmall.user.dto.UserDTO;
import io.elice.shoppingmall.user.entity.User;
import io.elice.shoppingmall.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean isEmailDuplicate(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public UserDTO createUser(UserDTO userDTO) {
        // 비밀번호 확인 로직 추가
        if (!userDTO.getPassword().equals(userDTO.getPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        User user = new User();
        user.setLevel(userDTO.getLevel());
        user.setEmail(userDTO.getEmail());
        user.setUsername(userDTO.getUsername());
        user.setPhNum(userDTO.getPhNum());
        user.setAddress(userDTO.getAddress());
        user.setNickname(userDTO.getNickname());
        user.setAdmin(userDTO.isAdmin());
        user.setTotalSpent(userDTO.getTotalSpent());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        User savedUser = userRepository.save(user);
        return new UserDTO(savedUser);
    }
}
