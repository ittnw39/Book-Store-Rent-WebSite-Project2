package io.elice.shoppingmall.order.DTO;

import io.elice.shoppingmall.order.entity.OrderOption;
import io.elice.shoppingmall.order.entity.Orders;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
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

    private String request;

    private List<OrderLineDTO> orderLines;

    public OrderDTO(Orders order) {
        this.id = order.getId();
        this.orderDate = order.getOrderDate();
        this.orderStatus = order.getOrderStatus();
        this.totalAmount = order.getTotalAmount();
        this.discountRate = order.getDiscountRate();
        this.userAddress = order.getUserAddress();
        this.orderOption = order.getOrderOption();
        this.userId = order.getUser().getId();
        this.request = order.getRequest();
    }
}