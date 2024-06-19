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
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public String getOrdersPage() {
        return "/admin-orders/admin-orders.html";
    }

    @GetMapping("/all")// 모든 주문 조회
    @ResponseBody
    public ResponseEntity<Page<OrderDTO>> getAllOrders(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderDTO> orders = orderService.getAllOrders(pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}") //주문 아이디별 주문 상세 조회
    public String getOrderDetail(@PathVariable Long orderId, Model model) {
        OrderDTO orderDTO = orderService.getOrderById(orderId);
        model.addAttribute("order", orderDTO);
        return "/order/order-detail.html";
    }

    @PutMapping("/{orderId}") //주문 수정
    @ResponseBody
    public ResponseEntity<Orders> updateOrder(@PathVariable Long orderId, @RequestBody OrderDTO orderDTO) {
        Orders updatedOrder = orderService.updateOrder(orderId, orderDTO);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{orderId}") //주문 삭제
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}