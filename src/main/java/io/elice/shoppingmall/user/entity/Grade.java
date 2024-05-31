package io.elice.shoppingmall.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "grade")
public class Grade {

    @Id
    private int level;

    @Column(nullable = false)
    private int rate;

    @Column(nullable = false, name = "level_instruction")
    private String levelInstruction;

}
