package io.elice.shoppingmall.cart.service;

import io.elice.shoppingmall.cart.dto.CartDetailDto;
import io.elice.shoppingmall.cart.dto.CartItemDto;
import io.elice.shoppingmall.cart.entity.Cart;
import io.elice.shoppingmall.cart.entity.CartItem;
import io.elice.shoppingmall.cart.repository.CartItemRepository;
import io.elice.shoppingmall.cart.repository.CartRepository;
import io.elice.shoppingmall.product.entity.Book;
import io.elice.shoppingmall.product.repository.BookRepository;
import io.elice.shoppingmall.user.entity.User;
import io.elice.shoppingmall.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    // 장바구니에 상품 추가
    public Long addCart(CartItemDto cartItemDto, String email) {
        Book book = bookRepository.findById(cartItemDto.getBookId())
            .orElseThrow(EntityNotFoundException::new);
        User user = userRepository.findByEmail(email)
            .orElseThrow(EntityNotFoundException::new);

        Cart cart = cartRepository.findByUser(user)
            .orElseGet(() -> {
                Cart newCart = Cart.createCart(user);
                cartRepository.save(newCart);
                return newCart;
            });

        CartItem savedCartItem = cartItemRepository.findByCartAndBook(cart, book)
            .map(cartItem -> {
                cartItem.addQuantity(cartItemDto.getQuantity());
                return cartItemRepository.save(cartItem);
            })
            .orElseGet(() -> {
                CartItem cartItem = CartItem.createCartItem(cart, book, cartItemDto.getQuantity());
                return cartItemRepository.save(cartItem);
            });

        return savedCartItem.getId();
    }

    // 장바구니 목록 조회
    @Transactional(readOnly = true)
    public List<CartDetailDto> getCartList(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(EntityNotFoundException::new);

        Cart cart = cartRepository.findByUser(user)
            .orElse(null);

        if (cart == null) {
            return new ArrayList<>();
        }

        return cartItemRepository.findCartDetailDtoList(cart.getId());
    }

    // 장바구니 상품 유효성 검사
    @Transactional(readOnly = true)
    public boolean validateCartItem(Long cartItemId, String email) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(EntityNotFoundException::new);

        User user = userRepository.findByEmail(email)
            .orElseThrow(EntityNotFoundException::new);

        return cartItem.getCart().getUser().equals(user);
    }

    // 장바구니 상품 수량 업데이트
    public void updateCartItemQuantity(Long cartItemId, int quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(EntityNotFoundException::new);

        cartItem.updateQuantity(quantity);
        cartItemRepository.save(cartItem);
    }

    // 장바구니 상품 삭제
    public void deleteCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(EntityNotFoundException::new);
        cartItemRepository.delete(cartItem);
    }
}