package io.elice.shoppingmall.order.entity;

import io.elice.shoppingmall.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_date")
    private Date orderDate;

    @Column(name = "order_status")
    private String orderStatus;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "discount_rate")
    private BigDecimal discountRate;

    @Column(name = "user_address")
    private String userAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_option")
    private OrderOption orderOption;

    @Column(name= "request")
    private String request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLine> orderLine = new ArrayList<>();;
}
