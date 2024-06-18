package io.elice.shoppingmall.order.controller;

import io.elice.shoppingmall.order.DTO.OrderLineDTO;
import io.elice.shoppingmall.order.entity.OrderLine;
import io.elice.shoppingmall.order.service.OrderLineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/orderLine")
@RequiredArgsConstructor
public class OrderLineController {

    private final OrderLineService orderLineService;

    @PostMapping("/create")
    public ResponseEntity<OrderLine> createOrderLine(@RequestBody OrderLineDTO orderLineDTO){
        OrderLine createdOrderLine = orderLineService.createOrderLine(orderLineDTO);
        return ResponseEntity.ok(createdOrderLine);
    }

    @GetMapping("/{orderId}") //주문 아이디별 주문 상세내역 조회
    public ResponseEntity<OrderLineDTO> getOrderDetail(@PathVariable Long orderId, Model model) {
        OrderLineDTO orderLineDTO = orderLineService.getOrderLineById(orderId);
        return ResponseEntity.ok(orderLineDTO);
    }

    @PutMapping("/{orderId}") //주문 정보 수정
    public ResponseEntity<OrderLine> updateOrderLine(@PathVariable Long orderId, @RequestBody OrderLineDTO orderLineDTO) {
        OrderLine updatedOrderLine = orderLineService.updateOrderLine(orderId, orderLineDTO);
        return ResponseEntity.ok(updatedOrderLine);
    }
}