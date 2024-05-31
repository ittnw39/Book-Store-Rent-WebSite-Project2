package java.io.elice.shoppingmall.cart.repository;

import io.elice.shoppingmall.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public class CartRepository extends JpaRepository<Cart, Long> {
    // 로그인한 회원의 Cart를 찾기 위한 메소드 추가// 로그인한 회원의 Cart를 찾기 위한 메소드 추가
    Cart findByMemberId(Long memberId);
}
