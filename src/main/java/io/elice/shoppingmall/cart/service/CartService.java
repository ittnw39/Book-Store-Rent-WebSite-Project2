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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional
public class CartService {
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Autowired
    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

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

        Optional<CartItem> savedCartItemOpt = cartItemRepository.findByCartAndBook(cart, book);

        if (savedCartItemOpt.isPresent()) {
            CartItem savedCartItem = savedCartItemOpt.get();
            savedCartItem.addQuantity(cartItemDto.getCount());
            cartItemRepository.save(savedCartItem);
            return savedCartItem.getId();
        } else {
            CartItem cartItem = CartItem.createCartItem(cart, book, cartItemDto.getCount());
            cartItemRepository.save(cartItem);
            return cartItem.getId();
        }
    }

//    @Transactional(readOnly = true)
//    public List<CartDetailDto> getCartList(String email){
//        List<CartDetailDto> cartDetailDtoList = new ArrayList<>();
//
//        Optional<User> user = userRepository.findByEmail(email);
//        Optional<Cart> cart = cartRepository.findByUser(user.getId())
//        if(cart == null){
//            return cartDetailDtoList;
//        }
//
//        cartDetailDtoList = cartItemRepository.findCartDetailDtoList(cart.getId());
//
//        return cartDetailDtoList;
//    }

}
