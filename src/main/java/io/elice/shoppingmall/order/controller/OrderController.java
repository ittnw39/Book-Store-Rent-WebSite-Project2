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

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping // 주문 정보 입력 페이지로 이동
    public String showOrderForm() {
        return "/order/order.html";
    }

    @PostMapping(path= "/create",  consumes = "application/json") // 상품 주문하기
    public ResponseEntity<?> createOrder(@RequestBody OrderDTO orderDTO) {
        Orders createdOrder = orderService.createOrder(orderDTO);
        return ResponseEntity.ok(createdOrder); //주문 완료 페이지
    }

    @GetMapping("/{userId}") //사용자별 주문 내역 조회
    public String getAllOrders(@PathVariable Long userId, Model model, @RequestParam(name= "page", defaultValue = "0") int page, @RequestParam(name = "size", defaultValue = "10") int size) { //사용자별 주문내역 조회
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderDTO> orders = orderService.getOrdersByUserId(userId, pageable);
        model.addAttribute("orders", orders);
        return "account-orders/account-orders"; //마이페이지->주문정보조회 페이지
    }

    @PutMapping("/{orderId}") //주문 정보 수정
    public ResponseEntity<Orders> updateOrder(@PathVariable Long orderId, @RequestBody OrderDTO orderDTO) {
        Orders updatedOrder = orderService.updateOrder(orderId, orderDTO);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{orderId}") //주문 내역 삭제
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}