package io.elice.shoppingmall.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import io.elice.shoppingmall.order.DTO.OrderDTO;
import io.elice.shoppingmall.order.service.OrderService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping // 주문 정보 입력 페이지로 이동
    public String showOrderForm() {
        return "order/order";
    }

    @PostMapping("/create") // 상품 주문하기
    public String createOrder(@RequestBody OrderDTO orderDTO) {
        orderService.createOrder(orderDTO);
        return "order-complete/order-complete"; //주문 완료 페이지
    }

    @GetMapping("/{userId}") //사용자별 주문 내역 조회
    public String getAllOrders(@PathVariable Long userId, Model model, @RequestParam(name= "page", defaultValue = "0") int page, @RequestParam(name = "size", defaultValue = "10") int size) { //사용자별 주문내역 조회
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderDTO> orders = orderService.getOrdersByUserId(userId, pageable);
        model.addAttribute("orders", orders);
        return "account-orders/account-orders"; //마이페이지->주문정보조회 페이지
    }

    @DeleteMapping("/{orderId}") //주문 내역 삭제
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}