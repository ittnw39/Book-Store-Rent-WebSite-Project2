package io.elice.shoppingmall.order.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderLineBookDTO {

    @NotNull
    private int quantity;

    @NotNull
    private Long bookId;

    @NotNull
    private Long orderLineId;
}
