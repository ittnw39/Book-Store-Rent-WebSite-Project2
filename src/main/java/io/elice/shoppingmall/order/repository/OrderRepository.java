package io.elice.shoppingmall.order.repository;

import io.elice.shoppingmall.order.DTO.OrderDTO;
import io.elice.shoppingmall.order.entity.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {

    Optional<Orders> findById(Long id); //주문 아이디에 따른 주문 반환 - 관리자

    @Override //모든 주문을 페이지 단위로 반환 - 관리자
    Page<Orders> findAll(Pageable pageable);

    // 특정 사용자 아이디에 따른 모든 주문을 페이지 단위로 반환 - 관리자, 사용자
    Page<Orders> findByUserId(Long userId, Pageable pageable);

    List<Orders> findAllByUserId(Long userId);
}
