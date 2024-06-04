package io.elice.shoppingmall.order.DTO;

import io.elice.shoppingmall.order.entity.OrderLineBook;
import io.elice.shoppingmall.order.entity.Orders;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderLineDTO {

    @NotNull
    private Long id;

    @NotNull
    private int quantity;

    @NotNull
    private BigDecimal price;

    private BigDecimal discountRate;

    @NotNull
    private Orders orderId;

    private List<OrderLineBook> orderLineBooks;
}
