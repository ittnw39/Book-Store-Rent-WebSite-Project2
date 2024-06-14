package io.elice.shoppingmall.cart.entity;

import io.elice.shoppingmall.product.entity.Book;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Entity
@Table(name = "cart_item")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdDate;

    @Column(nullable = false)
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;


    // 생성자
    public CartItem() {
        this.createdDate = new Date(); // 현재 날짜 및 시간으로 초기화
    }

    // 매개변수를 받는 생성자
    public CartItem(Cart cart, Book book) {
        this.cart = cart;
        this.book = book;
        this.quantity = 0;
    }

    public static CartItem createCartItem(Cart cart, Book book, int quantity) {
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setBook(book);
        cartItem.setQuantity(quantity);
        return cartItem;
    }


    public void addQuantity(int quantity){
        this.quantity += quantity;
    }

    public void updateQuantity(int quantity){
        this.quantity = quantity;
    }
}
