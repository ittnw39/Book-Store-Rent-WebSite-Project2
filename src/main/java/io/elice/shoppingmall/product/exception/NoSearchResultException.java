package io.elice.shoppingmall.product.exception;

public class NoSearchResultException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "검색 결과가 없습니다.";

    public NoSearchResultException() {
        super(DEFAULT_MESSAGE);
    }
}
