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
import java.util.stream.Collectors;


@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    @GetMapping("/cart")
    public String viewCartPage(Model model, Principal principal, RedirectAttributes redirectAttributes) {

        if(principal == null){
            return "page-not-found.html";
        }
        User user = getUserFromPrincipal(principal);
        Cart cart = cartService.getOrCreateCart(user);
        List<CartDetailDto> cartItems = cartService.getCartDetails(cart.getId());
        model.addAttribute("cartItems", cartItems);
        return "cart/cart.html";
    }

    @GetMapping("/api/cart")
    @ResponseBody
    public List<CartDetailDto> getCartItems(Principal principal) {
        User user = getUserFromPrincipal(principal);
        Cart cart = cartService.getOrCreateCart(user);
        return cartService.getCartDetails(cart.getId());
    }

    @PostMapping("/cart")
    public ResponseEntity<String> addCart(@RequestBody CartItemDto cartItemDto, Principal principal) {
        User user = getUserFromPrincipal(principal);
        cartService.addCart(user.getId(), cartItemDto);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/cart/item/{cartItemId}")
    public ResponseEntity<String> updateCartItemQuantity(@PathVariable("cartItemId") Long cartItemId, @RequestParam("quantity") int quantity, Principal principal) {
        User user = getUserFromPrincipal(principal);
        cartService.updateCartItemQuantity(cartItemId, quantity);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/cart/item/{cartItemId}")
    public ResponseEntity<String> deleteCartItem(@PathVariable("cartItemId") Long cartItemId, Principal principal) {
        User user = getUserFromPrincipal(principal);
        cartService.deleteCartItem(cartItemId);
        return ResponseEntity.ok().build();
    }




    private User getUserFromPrincipal(Principal principal) {
        String username = principal.getName();
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new EntityNotFoundException("이메일로 사용자를 찾을 수 없습니다: " + username));
    }

}
