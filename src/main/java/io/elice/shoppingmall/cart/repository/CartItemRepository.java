package io.elice.shoppingmall.cart.repository;

import io.elice.shoppingmall.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

}
