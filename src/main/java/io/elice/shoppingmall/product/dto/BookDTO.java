package io.elice.shoppingmall.product.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.elice.shoppingmall.category.entity.Category;
import io.elice.shoppingmall.product.entity.Author;
import jakarta.persistence.CascadeType;
import jakarta.persistence.ManyToOne;
import java.util.Date;
import lombok.Data;

@Data
public class BookDTO {

    private Long id;
    private String title;
    private String publisher;
    private Date publishedDate;
    private int price;
    private String description;
    private String imageURL;
    private int totalStockQuantity;
    private int page;

    @JsonBackReference(value = "authorReference")
    private Author author;

    @JsonBackReference(value = "categoryReference")
    private Category category;
}
