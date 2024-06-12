package io.elice.shoppingmall.cart.controller;

import io.elice.shoppingmall.cart.dto.CartDetailDto;
import io.elice.shoppingmall.cart.dto.CartItemDto;
import io.elice.shoppingmall.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    // 장바구니에 상품 추가
    @PostMapping("/items")
    public ResponseEntity<?> addCartItem(@RequestBody @Valid CartItemDto cartItemDto, BindingResult bindingResult, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        try {
            String email = authentication.getName();
            Long cartItemId = cartService.addCart(cartItemDto, email);
            return ResponseEntity.ok(cartItemId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 장바구니 조회
    @GetMapping
    public ResponseEntity<?> getCartList(Authentication authentication) {
        String email = authentication.getName();

        try {
            List<CartDetailDto> cartDetailList = cartService.getCartList(email);
            return ResponseEntity.ok(cartDetailList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 장바구니 상품 수량 업데이트
    @PatchMapping("/items/{cartItemId}")
    public ResponseEntity<?> updateCartItem(@PathVariable("cartItemId") Long cartItemId, @RequestParam int quantity, Authentication authentication) {
        String email = authentication.getName();

        if (!cartService.validateCartItem(cartItemId, email)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("수정 권한이 없습니다.");
        }

        cartService.updateCartItemQuantity(cartItemId, quantity);
        return ResponseEntity.ok(cartItemId);
    }

    // 장바구니 상품 삭제
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<?> deleteCartItem(@PathVariable("cartItemId") Long cartItemId, Authentication authentication) {
        String email = authentication.getName();

        if (!cartService.validateCartItem(cartItemId, email)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없습니다.");
        }

        cartService.deleteCartItem(cartItemId);
        return ResponseEntity.ok(cartItemId);
    }
}
