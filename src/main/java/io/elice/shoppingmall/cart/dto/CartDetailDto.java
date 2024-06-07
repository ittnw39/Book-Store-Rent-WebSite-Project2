package io.elice.shoppingmall.cart.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartDetailDto {
    private Long bookDetailId;
    private String title;
    private int price;
    private int quantity;
    private String imageURL;

    public CartDetailDto(Long bookDetailId, String title, int price, int quantity, String imageURL) {
        this.bookDetailId = bookDetailId;
        this.title = title;
        this.price = price;
        this.quantity = quantity;
        this.imageURL = imageURL;
    }
}