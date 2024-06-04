package io.elice.shoppingmall.order.controller;

import io.elice.shoppingmall.order.DTO.OrderLineBookDTO;
import io.elice.shoppingmall.order.DTO.OrderLineDTO;
import io.elice.shoppingmall.order.entity.OrderLine;
import io.elice.shoppingmall.order.service.OrderLineBookService;
import io.elice.shoppingmall.order.service.OrderLineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/orders/{orderId}/order-lines")
@RequiredArgsConstructor
public class OrderLineController {

    private final OrderLineService orderLineService;
    private final OrderLineBookService orderLineBookService;

    @GetMapping("/{orderLineId}") //주문 아이디별 주문 상세내역 조회
    public String getOrderDetail(@PathVariable Long orderId, @PathVariable Long orderLineId, Model model) {
        OrderLineDTO orderLineDTO = orderLineService.getOrderLineById(orderId);
        OrderLineBookDTO orderLineBookDTO = orderLineBookService.getBookByOrderLineId(orderLineId);
        model.addAttribute("orderLineBook", orderLineBookDTO);
        model.addAttribute("orderLine", orderLineDTO);
        return "order/order-detail/{orderId}";
    }

    @PutMapping //주문 정보 수정
    public ResponseEntity<OrderLine> updateOrderLine(@PathVariable Long orderId, @RequestBody OrderLineDTO orderLineDTO) {
        OrderLine updatedOrderLine = orderLineService.updateOrderLine(orderId, orderLineDTO);
        return ResponseEntity.ok(updatedOrderLine);
    }
}
