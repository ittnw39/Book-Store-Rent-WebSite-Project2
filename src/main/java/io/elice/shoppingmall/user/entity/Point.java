package io.elice.shoppingmall.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "point")
@Getter
@Setter
@AllArgsConstructor
public class Point {

    @Column(nullable = false, name = "point_stack")
    private Long pointStack;

    @Id
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
