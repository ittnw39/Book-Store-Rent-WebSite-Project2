package io.elice.shoppingmall.cart.controller;

import io.elice.shoppingmall.cart.dto.CartItemDto;
import io.elice.shoppingmall.cart.entity.Cart;
import io.elice.shoppingmall.cart.service.CartService;
import io.elice.shoppingmall.user.entity.User;
import io.elice.shoppingmall.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;

//되는코드 !
//원래코드
@Controller

@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    @GetMapping("/cart")
    public String viewCartPage(Model model, Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);

        User user = userRepository.findById(userId)
                .orElseThrow(EntityNotFoundException::new);

        Cart cart = cartService.getOrCreateCart(user);

        model.addAttribute("cartItems", cartService.getCartItems(cart));

        return "cart/cart.html";
    }

    @PostMapping("/cart/add")
    public String addCart(@RequestBody CartItemDto cartItemDto, Principal principal) {
        if (principal == null) {
            return "redirect:/login"; // 로그인 페이지로 리다이렉트
        }

        String username = principal.getName();
        System.out.println("Username retrieved from principal: " + username);

        User user = userRepository.findByEmail(username)
                .orElseThrow(EntityNotFoundException::new);
        System.out.println("Retrieved user: " + user);

        System.out.println("Adding book to cart. Book ID: " + cartItemDto.getBookId() + ", Quantity: " + cartItemDto.getQuantity());
        cartService.addCart(user.getId(), cartItemDto);

        return "redirect:/cart/cart.html";
    }

    private Long getUserIdFromPrincipal(Principal principal) {
        String username = principal.getName();
        System.out.println("Username retrieved from principal: " + username);

        User user = userRepository.findByEmail(username)
                .orElseThrow(EntityNotFoundException::new);
        System.out.println("Retrieved user: " + user);

        return user.getId();
    }


}