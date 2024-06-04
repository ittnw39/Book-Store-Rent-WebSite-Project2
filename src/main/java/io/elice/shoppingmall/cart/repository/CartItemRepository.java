package io.elice.shoppingmall.cart.repository;

import io.elice.shoppingmall.cart.dto.CartDetailDto;
import io.elice.shoppingmall.cart.entity.Cart;
import io.elice.shoppingmall.cart.entity.CartItem;
import io.elice.shoppingmall.product.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // 특정 장바구니의 모든 장바구니 항목을 찾는 메서드
    List<CartItem> findByCart(Cart cart);

    // 특정 장바구니와 책으로 장바구니 항목을 찾는 메서드 (옵션)
    Optional<CartItem> findByCartAndBook(Cart cart, Book book);

    @Query("select new io.elice.shoppingmall.cart.dto.CartDetailDto(ci.id, i.title, i.price, ci.quantity, im.imgUrl) " +
            "from CartItem ci, ItemImg im " +
            "join ci.book i " +
            //"join ItemImg im on im.book.id = i.id " +
            "where ci.cart.id = :cartId " +
            "and im.repimgYn = 'Y' " +
            "order by ci.createdDate desc")

    List<CartDetailDto> findCartDetailDtoList(@Param("cartId") Long cartId);
}