package io.elice.shoppingmall.cart.service;

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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> cartRepository.save(new Cart(user)));
    }

    public List<CartItem> getCartItems(Cart cart) {
        return cartItemRepository.findByCart(cart);
    }

    @Transactional
    public Long addCart(Long userId, CartItemDto cartItemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(EntityNotFoundException::new);
        Book book = bookRepository.findById(cartItemDto.getBookId())
                .orElseThrow(EntityNotFoundException::new);
        Cart cart = getOrCreateCart(user);

        // 이미 장바구니에 해당 상품이 있는지 확인
        Optional<CartItem> existingCartItemOpt = cartItemRepository.findByCartAndBook(cart, book);

        if (existingCartItemOpt.isPresent()) {
            // 이미 있는 경우, 수량을 증가시킴
            CartItem existingCartItem = existingCartItemOpt.get();
            existingCartItem.addQuantity(cartItemDto.getQuantity());
            cartItemRepository.save(existingCartItem);
            return existingCartItem.getId();
        } else {
            // 없는 경우, 새로운 카트 아이템을 생성하여 저장
            CartItem newCartItem = new CartItem(cart, book);
            newCartItem.setQuantity(cartItemDto.getQuantity());
            CartItem savedCartItem = cartItemRepository.save(newCartItem);
            return savedCartItem.getId();
        }
    }
}