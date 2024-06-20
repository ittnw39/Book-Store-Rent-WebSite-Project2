package io.elice.shoppingmall.order.controller;

import io.elice.shoppingmall.order.DTO.OrderLineDTO;
import io.elice.shoppingmall.order.entity.OrderLine;
import io.elice.shoppingmall.order.service.OrderLineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/{orderId}") //주문 아이디별 주문 라인 조회
    @ResponseBody
    public ResponseEntity<List<OrderLineDTO>> getOrderDetail(@PathVariable Long orderId) {
        List<OrderLineDTO> orderLineDTOs = orderLineService.getOrderLineByOrderId(orderId);
        return ResponseEntity.ok(orderLineDTOs);
    }

}