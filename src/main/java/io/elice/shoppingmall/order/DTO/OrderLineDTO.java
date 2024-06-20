package io.elice.shoppingmall.order.DTO;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

    @JsonBackReference
    private Long orderId;

    private List<OrderLineBookDTO> orderLineBooks;
}
