package io.elice.shoppingmall.order.DTO;

import io.elice.shoppingmall.order.entity.OrderLine;
import io.elice.shoppingmall.product.entity.Book;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderLineBookDTO {

    @NotNull
    private int quantity;

    @NotNull
    private Book bookId;

    @NotNull
    private OrderLine orderLineId;
}
