package io.elice.shoppingmall.cart.repository;

import io.elice.shoppingmall.cart.entity.Cart;
import java.util.Optional;

import io.elice.shoppingmall.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    // 사용자로부터 장바구니를 찾는 메서드
    Optional<Cart> findByUser(User user);
}