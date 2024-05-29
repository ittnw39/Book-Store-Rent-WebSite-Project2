package io.elice.shoppingmall.order.entity;

import io.elice.shoppingmall.product.entity.Book;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_line_book")
public class OrderLineBook {

    private int quantity;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @Id
    @ManyToOne
    @JoinColumn(name = "order_line_id")
    private OrderLine orderLine;
}
