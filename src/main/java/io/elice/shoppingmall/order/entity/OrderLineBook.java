package io.elice.shoppingmall.order.entity;

import io.elice.shoppingmall.product.entity.Book;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "order_line_book")
@AllArgsConstructor
@NoArgsConstructor
@IdClass(OrderLineBookKey.class)
public class OrderLineBook {

    private int quantity;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "book_id")
    private Book book;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "order_line_id")
    private OrderLine orderLine;

}
