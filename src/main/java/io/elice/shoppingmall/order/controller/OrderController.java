package io.elice.shoppingmall.order.controller;

import io.elice.shoppingmall.order.DTO.OrderDTO;
import io.elice.shoppingmall.order.entity.Orders;
import io.elice.shoppingmall.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping // 주문 정보 입력 페이지로 이동
    public String showOrderForm() {
        return "/order/order.html";
    }

    @GetMapping("/account")
    public String accountPage() {
        return "/account-orders/account-orders.html";
    }

    @GetMapping("/detail")
    public String detailPage() {
        return "/order/order-detail.html";
    }

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody OrderDTO orderDTO) {
        Orders createdOrder = orderService.createOrder(orderDTO);
        return ResponseEntity.ok(createdOrder); // OrderMapper는 엔티티를 DTO로 변환
    }

    @GetMapping("/{userId}") //사용자별 주문 내역 조회
    @ResponseBody
    public ResponseEntity<Page<OrderDTO>> getAllOrders(@PathVariable Long userId, @RequestParam(name= "page", defaultValue = "0") int page, @RequestParam(name = "size", defaultValue = "10") int size) { //사용자별 주문내역 조회
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderDTO> orders = orderService.getOrdersByUserId(userId, pageable);
        return ResponseEntity.ok(orders); //마이페이지->주문정보조회 페이지
    }

    @GetMapping("/details/{orderId}") // 주문 아이디별 주문 상세 조회 (JSON 응답)
    @ResponseBody
    public ResponseEntity<OrderDTO> getOrderDetail(@PathVariable Long orderId) {
        OrderDTO orderDTO = orderService.getOrderDetails(orderId);
        return ResponseEntity.ok(orderDTO);
    }

//    @GetMapping("/details/{orderId}") //주문 아이디별 주문 상세 조회
//    public String getOrderDetail(@PathVariable Long orderId, Model model) {
//        OrderDTO orderDTO = orderService.getOrderDetails(orderId);
//        model.addAttribute("order", orderDTO);
//        return "/order/order-detail.html";
//    }

    @PutMapping("/{orderId}") //주문상태 수정
    public ResponseEntity<Orders> updateOrderStatus(@PathVariable Long orderId, @RequestBody OrderDTO orderDTO) {
        Orders updatedOrder = orderService.updateOrder(orderId, orderDTO);
        return ResponseEntity.ok(updatedOrder);
    }

    @PutMapping("/address/{orderId}") //배송지 수정
    public ResponseEntity<Orders> updateOrderAddress(@PathVariable Long orderId, @RequestBody OrderDTO orderDTO) {
        Orders updatedAddress = orderService.updateAddress(orderId, orderDTO);
        return ResponseEntity.ok(updatedAddress);
    }

    @DeleteMapping("/{orderId}") //주문 삭제
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    // 새로운 메서드 추가 - 페이징 없이 사용자별 모든 주문 내역 조회
    @GetMapping("/user/{userId}/all")
    public ResponseEntity<List<OrderDTO>> getAllOrdersWithoutPaging(@PathVariable Long userId) {
        List<OrderDTO> orders = orderService.getAllOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }
}