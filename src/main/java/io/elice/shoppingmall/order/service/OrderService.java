package io.elice.shoppingmall.order.service;

import io.elice.shoppingmall.order.DTO.OrderDTO;
import io.elice.shoppingmall.order.entity.Orders;
import io.elice.shoppingmall.order.mapper.OrderMapper;
import io.elice.shoppingmall.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Transactional //주문 생성
    public Orders createOrder(OrderDTO orderDTO) {
        Orders order = orderMapper.toOrderEntity(orderDTO);
        return orderRepository.save(order);
    }

    public OrderDTO getOrderById(Long id) { //주문 아이디로 조회
        Orders order = orderRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        return orderMapper.toOrderDTO(order);
    }

    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
            .map(orderMapper::toOrderDTO)
            .collect(Collectors.toList());
    }

    public Page<OrderDTO> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
            .map(orderMapper::toOrderDTO);
    }

    public Page<OrderDTO> getOrdersByUserId(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable)
            .map(orderMapper::toOrderDTO);
    }

    @Transactional
    public Orders updateOrder(Long id, OrderDTO orderDTO) {
        Orders order = orderRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        orderMapper.updateOrderFromDTO(orderDTO, order);
        return orderRepository.save(order);
    }

    @Transactional
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}
