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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    @Autowired
    private final BookRepository bookRepository;
    private final UserRepository userRepository;



    // 사용자의 장바구니를 가져오거나 없으면 생성
    public Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> createCartForUser(user));
    }

    // 장바구니에 상품을 추가 , switch 문으로 변경함
    public void addCart(Long userId, CartItemDto cartItemDto) {
        User user = new User();
        user.setId(userId);

        Cart cart = getOrCreateCart(user);

        Book book = bookRepository.findById(cartItemDto.getBookId())
                .orElseThrow(() -> new EntityNotFoundException("책을 찾을 수 없습니다: " + cartItemDto.getBookId()));

        Optional<CartItem> existingCartItemOptional = cartItemRepository.findByCartAndBook(cart, book);

        existingCartItemOptional.ifPresentOrElse(
                // ifPresent case
                existingCartItem -> {
                    existingCartItem.addQuantity(cartItemDto.getQuantity());
                    cartItemRepository.save(existingCartItem);
                },
                // else case
                () -> {
                    CartItem newCartItem = new CartItem(cart, book);
                    newCartItem.setQuantity(cartItemDto.getQuantity());
                    cartItemRepository.save(newCartItem);
                }
        );
    }


    // 사용자를 위한 새로운 장바구니를 생성
    private Cart createCartForUser(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        return cartRepository.save(cart);
    }


    // 장바구니에서 상품을 삭제
    @Transactional
    public void deleteCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        cartItemRepository.delete(cartItem);
    }

    @Transactional
    public void updateCartItemQuantity(Long cartItemId, int quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("장바구니 항목을 찾을 수 없습니다"));
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
    }


    public List<CartDetailDto> getCartDetails(Long cartId) {
        return cartItemRepository.findCartDetailDtoList(cartId);
    }

}
