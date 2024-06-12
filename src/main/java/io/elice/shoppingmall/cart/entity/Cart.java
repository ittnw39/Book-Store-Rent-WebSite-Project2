package io.elice.shoppingmall.cart.entity;

import io.elice.shoppingmall.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="user_id")
    private User user; //member

    // 기본 생성자 추가
    public Cart() {
    }

    // 매개변수가 있는 생성자
    public Cart(User user) {
        this.user = user;
    }
}