package io.elice.shoppingmall.cart.controller;

import io.elice.shoppingmall.cart.repository.CartRepository;
import io.elice.shoppingmall.user.entity.User;
import io.elice.shoppingmall.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.Response;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import io.elice.shoppingmall.cart.dto.CartDetailDto;
import io.elice.shoppingmall.cart.dto.CartItemDto;
import io.elice.shoppingmall.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    //장바구니 담기
    @PostMapping("/cart/items")
    public @ResponseBody ResponseEntity<?> addCartItem(@RequestBody @Valid CartItemDto cartItemDto, BindingResult bindingResult, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage()).append(" ");
            }
            return new ResponseEntity<>(sb.toString().trim(), HttpStatus.BAD_REQUEST);
        }

        String email = authentication.getName();
        Long cartItemId;

        try {
            cartItemId = cartService.addCart(cartItemDto, email);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(cartItemId, HttpStatus.OK);
    }


    //장바구니 조회
    @GetMapping("/cart")
    public String getCartPage(Authentication authentication, Model model) {
        List<CartDetailDto> cartDetailList = new ArrayList<>();

        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();

            try {
                cartDetailList = cartService.getCartList(email);
            } catch (EntityNotFoundException e) {
                model.addAttribute("errorMessage", "사용자를 찾을 수 없습니다.");
            }
        } else {
            model.addAttribute("errorMessage", "로그인이 필요합니다.");
        }

        model.addAttribute("cartItems", cartDetailList);
        return "/cart/cart.html";
    }

    @PatchMapping("/cart/item/{cartItemId}")
    public @ResponseBody ResponseEntity<?> updateCartItem(@PathVariable("cartItemId") Long cartItemId, int quantity, Authentication authentication) {
        String email = authentication.getName();
        if (quantity <= 0) {
            return new ResponseEntity<>("최소 1개 이상 담아주세요", HttpStatus.BAD_REQUEST);
        }


        if (!cartService.validateCartItem(cartItemId, email)) {
            return new ResponseEntity<>("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        cartService.updateCartItemQuantity(cartItemId, quantity);
        return new ResponseEntity<>(cartItemId, HttpStatus.OK);
    }

    @DeleteMapping("/cart/item/{cartItemId}")
    public @ResponseBody ResponseEntity<?> deleteCartItem(@PathVariable("cartItemId") Long cartItemId, Authentication authentication) {
        String email =  authentication.getName();

        if (!cartService.validateCartItem(cartItemId, email)) {
            return new ResponseEntity<>("삭제 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        cartService.deleteCartItem(cartItemId);
        return new ResponseEntity<>(cartItemId, HttpStatus.OK);
    }


}
