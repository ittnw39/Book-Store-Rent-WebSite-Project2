package io.elice.shoppingmall.user.controller;
import io.elice.shoppingmall.user.dto.UserDTO;
import io.elice.shoppingmall.user.service.UserService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PatchMapping("/users")
    public ResponseEntity<Map<String, Object>> updateUserRole(@RequestBody Map<String, Object> requestBody) {
        String email = (String) requestBody.get("email");
        Boolean admin = (Boolean) requestBody.get("admin");

        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(email);
        userDTO.setAdmin(admin);

        UserDTO updatedUser = userService.updateUserRole(email, userDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "권한이 성공적으로 변경되었습니다.");
        response.put("updatedUser", updatedUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/users/{email}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable String email) {
        userService.deleteUser(email);
        Map<String, String> response = new HashMap<>();
        response.put("message", "회원 삭제가 완료되었습니다.");
        return ResponseEntity.ok(response);
    }







}


