package io.elice.shoppingmall.order.DTO;

import io.elice.shoppingmall.order.entity.OrderOption;
import io.elice.shoppingmall.user.entity.User;
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

    @NotNull
    private User userId;

    private List<OrderLineDTO> orderLines;
}