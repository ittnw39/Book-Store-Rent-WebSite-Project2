package io.elice.shoppingmall.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "grade")
@Getter
@Setter
@AllArgsConstructor
public class Grade {

    @Id
    private int level;

    @Column(nullable = false)
    private int rate;

    @Column(nullable = false, name = "level_instruction")
    private String levelInstruction;

}
