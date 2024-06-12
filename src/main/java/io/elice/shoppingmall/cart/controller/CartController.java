package io.elice.shoppingmall.cart.controller;

import io.elice.shoppingmall.cart.service.CartService;
import io.elice.shoppingmall.user.entity.User;
import io.elice.shoppingmall.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.Optional;

//원래코드
@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    @GetMapping("/cart")
    public String viewCartPage(Model model, Principal principal) {
        // Principal에서 사용자 ID 가져오기
        Long userId = getUserIdFromPrincipal(principal);

        // User 객체를 UserRepository를 통해 가져오기
        Optional<User> userOptional = userRepository.findById(userId);

        // User 객체가 존재하면 해당 객체를 사용하여 장바구니 생성 또는 확인
        userOptional.ifPresent(user -> cartService.createCart(user));

        // 사용자의 장바구니 페이지로 이동
        return "/cart/cart.html";
    }


    // Principal로부터 사용자 ID를 가져오는 메서드
    private Long getUserIdFromPrincipal(Principal principal) {
        // Principal을 통해 인증된 사용자 정보 가져오기
        String username = principal.getName();

        // User 객체를 UserRepository를 통해 가져오기
        Optional<User> userOptional = userRepository.findByEmail(username);

        // User 객체가 존재하면 해당 객체의 ID 반환, 그렇지 않으면 예외 처리
        return userOptional.orElseThrow(EntityNotFoundException::new).getId();
    }
}