package io.elice.shoppingmall.order.repository;

import io.elice.shoppingmall.order.entity.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {

    Optional<Orders> findById(Long id);

    //모든 주문을 페이지 단위로 반환
    Page<Orders> findAll(Long id, Pageable pageable);
}
