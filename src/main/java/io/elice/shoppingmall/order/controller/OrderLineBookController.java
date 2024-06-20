package io.elice.shoppingmall.order.controller;

import io.elice.shoppingmall.order.DTO.OrderLineBookDTO;
import io.elice.shoppingmall.order.entity.OrderLineBook;
import io.elice.shoppingmall.order.service.OrderLineBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/orderLineBook")
@RequiredArgsConstructor
public class OrderLineBookController {

    private final OrderLineBookService orderLineBookService;

    @PostMapping("/create") //상품이 주문에 생성됨
    public ResponseEntity<OrderLineBook> createOrderLine(@RequestBody OrderLineBookDTO orderLineBookDTO){
        OrderLineBook createdOrderLineBook = orderLineBookService.createOrderLineBook(orderLineBookDTO);
        return ResponseEntity.ok(createdOrderLineBook);
    }

    @GetMapping("/{orderLineId}") //주문라인 아이디별 상품 조회
    public ResponseEntity<OrderLineBookDTO> getOrderLineBook(@PathVariable Long orderLineId, Model model) {
        OrderLineBookDTO orderLineBookDTO = orderLineBookService.getOrderLineBookById(orderLineId);
        return ResponseEntity.ok(orderLineBookDTO);
    }

}
