package io.elice.shoppingmall.cart.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartDetailDto {
    private Long id;
    private Long bookDetailId;
    private String title;
    private int price;
    private int quantity;
    private String imageURL;

    public CartDetailDto(Long id, Long bookDetailId, String title, int price, int quantity, String imageURL) {
        this.id = id;
        this.bookDetailId = bookDetailId;
        this.title = title;
        this.price = price;
        this.quantity = quantity;
        this.imageURL = imageURL;
    }
}
