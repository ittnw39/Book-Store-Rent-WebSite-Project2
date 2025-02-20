package io.elice.shoppingmall.cart.repository;

import io.elice.shoppingmall.cart.entity.Cart;
import io.elice.shoppingmall.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUser(User userId);

}