package io.elice.shoppingmall.user.dto;

import io.elice.shoppingmall.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {
    private Long id; // 리뷰
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식에 맞지 않습니다.")
    private String email;
    private String username;
    private String phone_number;
    private String address;
    private String nickname;
    private Long totalSpent;
    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    private String password;
    @NotBlank(message = "비밀번호 확인은 필수 입력 값입니다.")
    private String passwordConfirm;
    private boolean admin;
    private Date createdAt;

    public UserDTO(User user) {
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.phone_number = user.getPhNum();
        this.address = user.getAddress();
        this.nickname = user.getNickname();
        this.password = user.getPassword();
        this.totalSpent = user.getTotalSpent();
        this.admin = user.isAdmin();
        this.createdAt = user.getCreatedAt();
    }
}
