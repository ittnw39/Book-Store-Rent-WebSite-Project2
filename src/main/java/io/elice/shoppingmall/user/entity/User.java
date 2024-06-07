package io.elice.shoppingmall.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.elice.shoppingmall.cart.entity.Cart;
import io.elice.shoppingmall.product.entity.Review;
import io.elice.shoppingmall.order.entity.Orders;
import io.elice.shoppingmall.product.entity.RentalUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int level;

    @Column(nullable = false, unique = true) //중복된 이메일 가입 방지
    private String email;

    @Column(nullable = true, name = "user_name")
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = true, name = "phone_number")
    private String phNum;

    @Column(nullable = true)
    private String address;

    @Column(nullable = true)
    private String nickname;

    @Column(nullable = false, name = "admin")
    private boolean isAdmin;

    @Column(nullable = true, name = "total_spent")
    private Long totalSpent;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, name = "created_at")
    private Date createdAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE)
    private WishList wishList;

    @OneToMany(mappedBy ="user")
    @JsonIgnore
    private List<Orders> orders;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Review> review;

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE)
    private Cart cart;

    @OneToOne(mappedBy = "user")
    private RentalUser rentalUser;

    @ManyToOne
    @JoinColumn(name = "grade")
    private Grade grade;
}
