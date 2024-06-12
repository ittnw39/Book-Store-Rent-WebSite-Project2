package io.elice.shoppingmall.cart.entity;

import io.elice.shoppingmall.product.entity.Book;
import io.elice.shoppingmall.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

@Entity
@Table(name = "cart_item")
@Getter
@Setter
public class CartItem {

    //장바구니에 담을 상품 엔티티

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book; //item = book

    @ManyToOne // 사용자와의 연관관계
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private int quantity; //count

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdDate; // 추가: 정렬을 위해 생성 시간 필드가 필요할 수 있음

    // 생성자
    public CartItem() {}

    // 매개변수를 받는 생성자
    public CartItem(Cart cart, Book book, User user, int quantity) {
        this.cart = cart;
        this.book = book;
        this.user = user; // 사용자 추가
        this.quantity = quantity;
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