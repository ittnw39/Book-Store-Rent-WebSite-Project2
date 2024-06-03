package io.elice.shoppingmall.user.entity;

import io.elice.shoppingmall.cart.entity.Cart;
import io.elice.shoppingmall.etc.entity.Review;
import io.elice.shoppingmall.order.entity.Orders;
import io.elice.shoppingmall.product.entity.RentalUser;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

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

    @Column(nullable = false, name = "user_name")
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, name = "phone_number")
    private String phNum;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false, name = "admin")
    private boolean isAdmin;

    @Column(nullable = false, name = "total_spent")
    private Long totalSpent;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, name = "created_at")
    private Date createdAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE)
    private WishList wishList;

    @OneToMany(mappedBy = "user")
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
