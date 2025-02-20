package io.elice.shoppingmall.product.entity;

import io.elice.shoppingmall.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "rental_user")
@Getter
@Setter
@AllArgsConstructor
public class RentalUser {

    @Id
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
