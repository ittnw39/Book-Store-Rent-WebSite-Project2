package io.elice.shoppingmall.user.controller;
import io.elice.shoppingmall.user.dto.UserDTO;
import io.elice.shoppingmall.user.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;


    @GetMapping
    public String adminPage() {
        return "/admin/admin.html";
    }


    @GetMapping("/users")
    public String adminUsers() {
        return "/admin-users/admin-users.html";
    }

    @GetMapping("/users/all")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PatchMapping("/users/{email}")
    public ResponseEntity<UserDTO> updateUserRole(@PathVariable String email, @RequestBody UserDTO userDTO) {
        UserDTO updatedUser = userService.updateUserRole(email, userDTO);
        return ResponseEntity.ok(updatedUser);
    }


    @DeleteMapping("/users/{email}")
    public ResponseEntity<String> deleteUser(@PathVariable String email) {
        userService.deleteUser(email);
        return ResponseEntity.ok("회원 삭제가 완료되었습니다.");
    }







}


