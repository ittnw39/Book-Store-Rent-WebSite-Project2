package io.elice.shoppingmall.cart.controller;

import io.elice.shoppingmall.cart.dto.CartDetailDto;
import io.elice.shoppingmall.cart.dto.CartItemDto;
import io.elice.shoppingmall.cart.entity.Cart;
import io.elice.shoppingmall.cart.service.CartService;
import io.elice.shoppingmall.user.entity.User;
import io.elice.shoppingmall.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequiredArgsConstructor
@Validated
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    // 에러 메시지를 static 상수로 정의
    private static final String UNAUTHORIZED_USER_MESSAGE = "인증되지 않은 사용자입니다.";
    private static final String USER_NOT_FOUND_MESSAGE = "이메일로 사용자를 찾을 수 없습니다: ";

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/cart")
    public String viewCartPage(Model model, Principal principal, RedirectAttributes redirectAttributes) {
        User user = getUserFromPrincipal(principal);
        Cart cart = cartService.getOrCreateCart(user);
        List<CartDetailDto> cartItems = cartService.getCartDetails(cart.getId());
        model.addAttribute("cartItems", cartItems);
        return "cart/cart.html";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/api/cart")
    @ResponseBody
    public List<CartDetailDto> getCartItems(Principal principal) {
        User user = getUserFromPrincipal(principal);
        Cart cart = cartService.getOrCreateCart(user);
        return cartService.getCartDetails(cart.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/cart")
    public ResponseEntity<String> addCart(@RequestBody @Valid CartItemDto cartItemDto, Principal principal) {
        User user = getUserFromPrincipal(principal);
        cartService.addCart(user.getId(), cartItemDto);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/cart/item/{cartItemId}")
    public ResponseEntity<String> updateCartItemQuantity(@PathVariable("cartItemId") Long cartItemId, @RequestParam("quantity") @Min(1) int quantity, Principal principal) {
        User user = getUserFromPrincipal(principal);
        cartService.updateCartItemQuantity(cartItemId, quantity);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/cart/item/{cartItemId}")
    public ResponseEntity<String> deleteCartItem(@PathVariable("cartItemId") Long cartItemId, Principal principal) {
        User user = getUserFromPrincipal(principal);
        cartService.deleteCartItem(cartItemId);
        return ResponseEntity.ok().build();
    }

    private User getUserFromPrincipal(Principal principal) {
        if (principal == null) {
            throw new IllegalArgumentException(UNAUTHORIZED_USER_MESSAGE);
        }
        String username = principal.getName();
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE + username));
    }
}