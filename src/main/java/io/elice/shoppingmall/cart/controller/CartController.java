package io.elice.shoppingmall.cart.controller;

import io.elice.shoppingmall.cart.dto.CartDetailDto;
import io.elice.shoppingmall.cart.dto.CartItemDto;
import io.elice.shoppingmall.cart.dto.CartOrderDto;
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
        return new ResponseEntity<>("상품이 장바구니에 추가되었습니다", HttpStatus.OK);
    }

    @PatchMapping("/cart/item/{cartItemId}")
    public ResponseEntity<String> updateCartItemQuantity(@PathVariable("cartItemId") Long cartItemId, @RequestParam("quantity") int quantity, Principal principal) {
        User user = getUserFromPrincipal(principal);
        cartService.updateCartItemQuantity(cartItemId, quantity);
        return ResponseEntity.ok("장바구니 항목 수량이 성공적으로 업데이트되었습니다");
    }

    @DeleteMapping("/cart/item/{cartItemId}")
    public ResponseEntity<String> deleteCartItem(@PathVariable("cartItemId") Long cartItemId, Principal principal) {
        User user = getUserFromPrincipal(principal);
        cartService.deleteCartItem(cartItemId);
        return ResponseEntity.ok("장바구니 항목이 성공적으로 삭제되었습니다");
    }

    //주문생성처리
//    @PostMapping("/cart/orders")
//    public ResponseEntity<String> createOrder(@RequestBody CartOrderDto cartOrderDto, Principal principal) {
//        User user = getUserFromPrincipal(principal);
//        List<Long> selectedItems = cartOrderDto.getCartOrderDtoList()
//                .stream()
//                .map(CartOrderDto::getCartItemId)
//                .collect(Collectors.toList());
//
//        Long orderId = cartService.createOrder(user, selectedItems);
//
//        return new ResponseEntity<>("주문이 성공적으로 생성되었습니다. 주문 ID: " + orderId, HttpStatus.CREATED);
//    }
//
//    @GetMapping("/api/cart/item/{itemId}")
//    @ResponseBody
//    public ResponseEntity<?> getItemById(@PathVariable Long itemId) {
//        try {
//            CartDetailDto item = cartService.findCartItemById(itemId); // itemId를 이용해 상품 정보를 가져오는 메서드
//            if (item == null) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("상품을 찾을 수 없습니다.");
//            }
//            return ResponseEntity.ok(item);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생");
//        }
//    }


    //주문 후 장바구니 비우는 코드
//    @PostMapping("/orders")
//    public ResponseEntity<String> createOrder(@RequestBody CartOrderDto cartOrderDto, Principal principal) {
//        User user = getUserFromPrincipal(principal);
//        List<Long> selectedItems = cartOrderDto.getCartOrderDtoList()
//                .stream()
//                .map(CartOrderDto::getCartItemId)
//                .collect(Collectors.toList());
//
//        Long orderId = cartService.createOrder(user, selectedItems);
//
//        // 주문 생성 후 장바구니 비우기
//        cartService.clearCart(user, selectedItems);
//
//        // 여기서 필요한 추가적인 작업을 수행할 수 있음 (예: 사용자에게 알림 보내기 등)
//
//        return new ResponseEntity<>("주문이 성공적으로 생성되었습니다. 주문 ID: " + orderId, HttpStatus.CREATED);
//    }

    private User getUserFromPrincipal(Principal principal) {
        String username = principal.getName();
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new EntityNotFoundException("이메일로 사용자를 찾을 수 없습니다: " + username));
    }

}
