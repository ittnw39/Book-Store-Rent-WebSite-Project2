package io.elice.shoppingmall.category.entity;

import io.elice.shoppingmall.product.entity.Book;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "category")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Name cannot be empty")
    private String name;

    @OneToMany(mappedBy = "category", cascade = CascadeType.REMOVE)
    private List<Book> books;
}
