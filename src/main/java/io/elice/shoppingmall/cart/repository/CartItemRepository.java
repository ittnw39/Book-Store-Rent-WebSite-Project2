package io.elice.shoppingmall.cart.repository;

import io.elice.shoppingmall.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    //장바구니에 들어갈 상품 저장, 조회
//    CartItem findByCartIdAndItemId(Long cartId, Long ItemId);
}
