package io.elice.shoppingmall.order.service;

import io.elice.shoppingmall.order.DTO.OrderDTO;
import io.elice.shoppingmall.order.entity.OrderLine;
import io.elice.shoppingmall.order.entity.OrderLineBook;
import io.elice.shoppingmall.order.entity.Orders;
import io.elice.shoppingmall.order.mapper.OrderMapper;
import io.elice.shoppingmall.order.repository.OrderLineBookRepository;
import io.elice.shoppingmall.order.repository.OrderLineRepository;
import io.elice.shoppingmall.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderLineRepository orderLineRepository;

    @Autowired
    private OrderLineBookRepository orderLineBookRepository;

    @Autowired
    private OrderMapper orderMapper;

    public Optional<Orders> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Page<Orders> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Transactional
    public Orders createOrder(OrderDTO orderDTO) {
        // OrderDTO를 Orders 엔티티로 변환
        Orders order = orderMapper.toOrderEntity(orderDTO);

        // Orders 엔티티 저장
        Orders savedOrder = orderRepository.save(order);

        // OrderLine DTO를 OrderLine 엔티티로 변환 및 저장
        List<OrderLine> orderLines = orderDTO.getOrderLines().stream()
                .map(orderLineDTO -> {
                    OrderLine orderLine = orderMapper.toOrderLineEntity(orderLineDTO);
                    orderLine.setOrders(savedOrder);

                    // OrderLineBook DTO를 OrderLineBook 엔티티로 변환 및 설정
                    List<OrderLineBook> orderLineBooks = orderLineDTO.getOrderLineBooks().stream()
                            .map(orderLineBookDTO -> {
                                OrderLineBook orderLineBook = orderMapper.toOrderLineBookEntity(orderLineBookDTO);
                                orderLineBook.setOrderLine(orderLine);
                                return orderLineBook;
                            }).collect(Collectors.toList());

                    orderLine.setOrderLineBooks(orderLineBooks);

                    return orderLineRepository.save(orderLine);
                })
                .collect(Collectors.toList());

        // OrderLineBook 엔티티 저장
        for (OrderLine orderLine : orderLines) {
            orderLine.getOrderLineBooks().forEach(orderLineBook -> {
                orderLineBookRepository.save(orderLineBook);
            });
        }

        return savedOrder;
    }

    @Transactional
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}
