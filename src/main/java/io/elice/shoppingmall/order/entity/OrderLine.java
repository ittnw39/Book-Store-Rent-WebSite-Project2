package io.elice.shoppingmall.order.entity;

import io.elice.shoppingmall.order.DTO.OrderLineBookDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_line")
public class OrderLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private int quantity;

    private BigDecimal price;

    @Column(name = "discount_rate")
    private BigDecimal discountRate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_id")
    private Orders orderId;

    @OneToMany(mappedBy = "orderLine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLineBook> orderLineBooks;

}