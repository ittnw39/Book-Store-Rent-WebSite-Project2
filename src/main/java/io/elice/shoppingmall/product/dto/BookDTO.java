package io.elice.shoppingmall.product.dto;

import io.elice.shoppingmall.category.entity.Category;
import io.elice.shoppingmall.product.entity.Author;
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
    private Author author;
    private Category category;
}
