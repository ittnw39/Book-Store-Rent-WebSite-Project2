package io.elice.shoppingmall.product.dto;

import lombok.Data;

@Data
public class ReviewDTO {

    private Long id;
    private String comment;
    private double rating;

    private BookDTO bookDTO;

//    private User user;
}
