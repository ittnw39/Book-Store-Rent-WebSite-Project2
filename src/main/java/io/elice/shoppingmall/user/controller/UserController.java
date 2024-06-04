package io.elice.shoppingmall.user.controller;

import io.elice.shoppingmall.user.dto.UserDTO;
import io.elice.shoppingmall.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserDTO userDTO) {
        // 이메일 중복 검사
        if (userService.isEmailDuplicate(userDTO.getEmail())) {
            return ResponseEntity.badRequest().body(null);
        }

        UserDTO createdUser = userService.createUser(userDTO);
        return ResponseEntity.ok(createdUser);
    }
}

