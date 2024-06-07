package io.elice.shoppingmall.cart.entity;

import io.elice.shoppingmall.order.entity.OrderOption;
import io.elice.shoppingmall.product.entity.Book;
import io.elice.shoppingmall.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cart")
@Getter
@Setter
@ToString //객체를 문자열로 출력

public class Cart {

    //장바구니 생성

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne (fetch = FetchType.EAGER)
    @JoinColumn(name="user_id")
    private User user; //member

    public static Cart createCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        return cart;
    }
}