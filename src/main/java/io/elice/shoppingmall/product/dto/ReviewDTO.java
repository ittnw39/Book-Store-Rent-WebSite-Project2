package io.elice.shoppingmall.product.dto;

import io.elice.shoppingmall.user.dto.UserDTO;
import lombok.Data;

import java.util.Date;

@Data
public class ReviewDTO {

    private Long id;
    private String comment;
    private Date createdAt;
    private int likes;

    private BookDTO bookDTO;
    private UserDTO userDTO;

}
