package io.elice.shoppingmall.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "point")
public class Point {

    @Column(nullable = false, name = "point_stack")
    private Long pointStack;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
