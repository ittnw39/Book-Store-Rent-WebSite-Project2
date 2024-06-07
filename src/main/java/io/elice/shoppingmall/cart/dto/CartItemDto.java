package io.elice.shoppingmall.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemDto {

    //상품 상세 페이지에서 장바구니에 담을 상품 id, 수량
    @NotNull(message = "상품 아이디는 필수 입력 값 입니다.")
    private Long bookId;

    @Min(value = 1, message = "최소 1개 이상 담아주세요")
    private int quantity;
}