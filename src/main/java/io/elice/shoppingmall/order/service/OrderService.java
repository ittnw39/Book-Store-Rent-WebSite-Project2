package io.elice.shoppingmall.order.service;

import io.elice.shoppingmall.order.DTO.OrderDTO;
import io.elice.shoppingmall.order.entity.OrderLine;
import io.elice.shoppingmall.order.entity.Orders;
import io.elice.shoppingmall.order.mapper.OrderMapper;
import io.elice.shoppingmall.order.repository.OrderRepository;
import io.elice.shoppingmall.product.entity.Book;
import io.elice.shoppingmall.product.repository.BookRepository;
import io.elice.shoppingmall.user.entity.User;
import io.elice.shoppingmall.user.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final BookRepository bookRepository;

    //주문 생성
    @Transactional
    public Orders createOrder(OrderDTO orderDTO) {
        User user = userRepository.findById(orderDTO.getUserId()).orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + orderDTO.getUserId()));
        Orders order = OrderMapper.INSTANCE.toOrderEntity(orderDTO, user);

        // OrderLine 리스트가 null이 아니면 추가
        if (orderDTO.getOrderLines() != null) {
            orderDTO.getOrderLines().forEach(orderLineDTO -> {
                OrderLine orderLine = OrderMapper.INSTANCE.toOrderLineEntity(orderLineDTO);
                orderLine.setOrders(order); // OrderLine에 Order 설정
                order.getOrderLine().add(orderLine);
            });
        }
        Orders savedOrder = orderRepository.save(order);

        if (savedOrder.getOrderLine() != null) {
            savedOrder.getOrderLine().forEach(orderLine -> {
                if (orderLine.getOrderLineBooks() != null) {
                    orderLine.getOrderLineBooks().forEach(orderLineBook -> {
                        Book book = orderLineBook.getBook();
                        if (book != null) {
                            book.setOrderCount(book.getOrderCount() + 1);
                            bookRepository.save(book);
                        }
                    });
                }
            });
        }
        return savedOrder;
    }

    public OrderDTO getOrderById(Long id) { //관리자 - 주문 아이디로 조회
        Orders order = orderRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        order.getOrderLine().forEach(line -> line.getOrderLineBooks().size());
        return orderMapper.toOrderDTO(order);
    }

    public Page<OrderDTO> getAllOrders(Pageable pageable) { //관리자 - 모든 주문조회
        return orderRepository.findAll(pageable)
            .map(orderMapper::toOrderDTO);
    }

    public Page<OrderDTO> getOrdersByUserId(Long userId, Pageable pageable) { //사용자 - 사용자 아이디별 주문 조회
        Page<Orders> orders = orderRepository.findByUserId(userId, pageable);
        orders.getContent().forEach(order -> order.getOrderLine().forEach(line -> line.getOrderLineBooks().size()));
        return orders.map(orderMapper::toOrderDTO);
    }

    public OrderDTO getOrderDetails(Long orderId) { // 관리자,사용자 - 주문 아이디로 주문 상세 조회
        Orders order = orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        order.getOrderLine().forEach(line -> line.getOrderLineBooks().size());
        return orderMapper.toOrderDTO(order);
    }

    @Transactional
    public Orders updateOrder(Long id, OrderDTO orderDTO) { //관리자, 사용자 - 주문 수정
        Orders order = orderRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("OrderId not found"));

        System.out.println("Order found: " + order);
        System.out.println("Updating order status to: " + orderDTO.getOrderStatus());

        order.setOrderStatus(orderDTO.getOrderStatus());

        Orders savedOrder = orderRepository.save(order);

        System.out.println("Order updated: " + savedOrder);
        return savedOrder;
    }

    //관리자, 사용자 - 주문 삭제 (수정)
    @Transactional
    public void deleteOrder(Long id) {
        Orders order = orderRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        //각 책의 주문 횟수 감소
        order.getOrderLine().forEach(orderLine -> {
            orderLine.getOrderLineBooks().forEach(orderLineBook -> {
                Book book = orderLineBook.getBook();
                book.setOrderCount(book.getOrderCount() - 1);
                bookRepository.save(book);
            });
        });
        orderRepository.delete(order);
    }

    public List<OrderDTO> getAllOrdersByUserId(Long userId) {
        List<Orders> orders = orderRepository.findAllByUserId(userId);
        return orders.stream()
            .map(order -> new OrderDTO(order))
            .collect(Collectors.toList());
    }

    @Transactional
    public Orders updateAddress(Long orderId, OrderDTO orderDTO) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("OrderId not found"));

        System.out.println("Order found: " + order);
        System.out.println("Updating order address to: " + orderDTO.getUserAddress());

        order.setUserAddress(orderDTO.getUserAddress());

        Orders savedOrder = orderRepository.save(order);

        System.out.println("Order updated: " + savedOrder);
        return savedOrder;
    }
}