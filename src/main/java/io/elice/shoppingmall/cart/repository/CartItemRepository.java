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

    //카트 아이디와 상품 아이디를 이용해서 상품이 장바구니에 들어있는지 조회
    List<CartItem> findByCart(Cart cart);
    Optional<CartItem> findByCartAndBook(Cart cart, Book book);

    @Query("select new io.elice.shoppingmall.cart.dto.CartDetailDto(ci.id, b.id, b.title, b.price, ci.quantity, b.imageURL) " +
            "from CartItem ci " +
            "join ci.book b " +
            "where ci.cart.id = :cartId " +
            "order by ci.createdDate desc")
    List<CartDetailDto> findCartDetailDtoList(@Param("cartId") Long cartId);

}