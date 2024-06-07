package io.elice.shoppingmall.user.security;

import io.elice.shoppingmall.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
@RequiredArgsConstructor
public class AdminUserInitializer implements CommandLineRunner {

    private final UserService userService;

    @Override
    public void run(String... args) throws Exception {
        // 관리자 계정이 존재하지 않으면 생성
        if (!userService.isEmailDuplicate("admin@example.com")){
            userService.createAdminUser("admin@example.com", "admin_password");
        }
    }
}
