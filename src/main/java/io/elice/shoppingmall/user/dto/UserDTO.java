package io.elice.shoppingmall.user.dto;

import io.elice.shoppingmall.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private int level;
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식에 맞지 않습니다.")
    private String email;
    private String username;
    private String phNum;
    private String address;
    private String nickname;
    private boolean isAdmin;
    private Long totalSpent;
    private String password;
    private String passwordConfirm;

    public UserDTO(User user) {
        this.id = user.getId();
        this.level = user.getLevel();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.phNum = user.getPhNum();
        this.address = user.getAddress();
        this.nickname = user.getNickname();
        this.isAdmin = user.isAdmin();
        this.totalSpent = user.getTotalSpent();
        this.password = user.getPassword();
    }
}
