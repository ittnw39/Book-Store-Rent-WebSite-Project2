package io.elice.shoppingmall.cart.repository;

import io.elice.shoppingmall.cart.entity.Cart;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
    // 로그인한 회원의 Cart를 찾기 위한 메소드 추가// 로그인한 회원의 Cart를 찾기 위한 메소드 추가
    Optional<Cart> findById(Long memberId);
}
