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
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식에 맞지 않습니다.")
    private String email;
    private String username;
    private String phNum;
    private String address;
    private String nickname;
    private Long totalSpent;
    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    private String password;
    @NotBlank(message = "비밀번호 확인은 필수 입력 값입니다.")
    private String passwordConfirm;

    public UserDTO(User user) {
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.phNum = user.getPhNum();
        this.address = user.getAddress();
        this.nickname = user.getNickname();
        this.password = user.getPassword();
        this.totalSpent = user.getTotalSpent();
    }
}
