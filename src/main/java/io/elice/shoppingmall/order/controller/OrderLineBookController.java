package io.elice.shoppingmall.order.controller;

import io.elice.shoppingmall.order.DTO.OrderLineBookDTO;
import io.elice.shoppingmall.order.service.OrderLineBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/orders/{orderId}")
@RequiredArgsConstructor
public class OrderLineBookController {

    private final OrderLineBookService orderLineBookService;

    @GetMapping("/{orderLineId}") //주문 상세내역별 상품목록 조회
    public String getOrderDetail(@PathVariable Long orderLineId, Model model) {
        OrderLineBookDTO orderLineBookDTO = orderLineBookService.getBookByOrderLineId(orderLineId);
        model.addAttribute("orderLine", orderLineBookDTO);
        return "order/order-detail/{orderId}";
    }

}
