package io.elice.shoppingmall.order.DTO;

import io.elice.shoppingmall.order.entity.OrderOption;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class OrderDTO {

    @NotNull
    private Long id;

    @NotNull
    private Date orderDate;

    @NotNull
    private String orderStatus;

    @NotNull
    private BigDecimal totalAmount;

    private BigDecimal discountRate;

    @NotNull
    private String userAddress;

    @NotNull
    private OrderOption orderOption;

    @Column(nullable = false)
    private Long userId;

    private List<OrderLineDTO> orderLines;
}