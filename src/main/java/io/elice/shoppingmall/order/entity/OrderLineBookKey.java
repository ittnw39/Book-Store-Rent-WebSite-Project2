package io.elice.shoppingmall.order.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class OrderLineBookKey implements Serializable {

    private Long book;
    private Long orderLine;

    public OrderLineBookKey() {}

    public OrderLineBookKey(Long book, Long orderLine) {
        this.book = book;
        this.orderLine = orderLine;
    }

    @Override
    public boolean equals(Object o) { //OrderLineBookKey 객체의 book과 orderLine 필드 값이 모두 같은지를 비교
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderLineBookKey that = (OrderLineBookKey) o;
        return Objects.equals(book, that.book) && Objects.equals(orderLine, that.orderLine);
    }

    @Override
    public int hashCode() { //book과 orderLine 필드 값을 기준으로 해시 코드를 생성
        return Objects.hash(book, orderLine);
    }
}
