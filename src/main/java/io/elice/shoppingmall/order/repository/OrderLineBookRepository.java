package io.elice.shoppingmall.order.repository;

import io.elice.shoppingmall.order.entity.OrderLineBook;
import io.elice.shoppingmall.order.entity.OrderLineBookKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderLineBookRepository extends JpaRepository<OrderLineBook, OrderLineBookKey> {

    // 특정 주문 라인 ID로 주문 라인 도서를 찾는 메소드
    OrderLineBook findByOrderLineId(Long orderLineId);

}

