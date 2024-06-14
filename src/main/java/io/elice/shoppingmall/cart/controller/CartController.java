package io.elice.shoppingmall.cart.controller;

import io.elice.shoppingmall.cart.dto.CartDetailDto;
import io.elice.shoppingmall.cart.dto.CartItemDto;
import io.elice.shoppingmall.cart.entity.Cart;
import io.elice.shoppingmall.cart.service.CartService;
import io.elice.shoppingmall.user.entity.User;
import io.elice.shoppingmall.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

//되는코드 !
//원래코드
@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    @GetMapping("/cart")
    public String viewCartPage(Model model, Principal principal, RedirectAttributes redirectAttributes) {
        User user = getUserFromPrincipal(principal);
        if (principal == null) {
            redirectAttributes.addFlashAttribute("message", "로그인이 필요합니다");
            return "redirect:/users/login";
        }

        Cart cart = cartService.getOrCreateCart(user);
        List<CartDetailDto> cartItems = cartService.getCartDetails(cart.getId());
        model.addAttribute("cartItems", cartItems);
        return "cart/cart.html";
    }

    @GetMapping("/api/cart")
    @ResponseBody
    public List<CartDetailDto> getCartItems(Principal principal) {
        User user = getUserFromPrincipal(principal);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다");
        }
        Cart cart = cartService.getOrCreateCart(user);
        return cartService.getCartDetails(cart.getId());
    }

    @PostMapping("/cart/add")
    public ResponseEntity<String> addCart(@RequestBody CartItemDto cartItemDto, Principal principal) {
        User user = getUserFromPrincipal(principal);
        if (user == null) {
            return new ResponseEntity<>("로그인이 필요합니다", HttpStatus.UNAUTHORIZED);
        }

        cartService.addCart(user.getId(), cartItemDto);

        return new ResponseEntity<>("Item added to cart", HttpStatus.OK);
    }

    @DeleteMapping("/cart/item/{cartItemId}")
    public ResponseEntity<String> deleteCartItem(@PathVariable("cartItemId") Long cartItemId, Principal principal) {
        if (principal == null) {
            return new ResponseEntity<>("로그인이 필요합니다", HttpStatus.UNAUTHORIZED);
        }

        try {
            cartService.deleteCartItem(cartItemId);
            return ResponseEntity.ok("Cart item deleted successfully");
        }catch (Exception e) {
            return new ResponseEntity<>("상품 삭제 중 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    private User getUserFromPrincipal(Principal principal) {
        if (principal == null) {
            return null;
        }
        String username = principal.getName();
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + username));
    }
}