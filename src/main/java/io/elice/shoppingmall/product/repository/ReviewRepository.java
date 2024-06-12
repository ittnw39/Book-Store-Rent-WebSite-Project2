package io.elice.shoppingmall.product.repository;

import io.elice.shoppingmall.product.entity.Book;
import io.elice.shoppingmall.product.entity.Review;
import io.elice.shoppingmall.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Review save(Review review);

    Optional<Review> findById(Long id);

    List<Review> findByBook(Book book);

    List<Review> findByUser(User user);

    @Query("SELECT r FROM Review r WHERE r.book = :book ORDER BY r.likes DESC")
    List<Review> findByBookOrderByLikesDesc(@Param("book") Book book);

    @Query("SELECT r FROM Review r WHERE r.book = :book ORDER BY r.createdAt DESC")
    List<Review> findByBookOrderByCreatedAtDesc(@Param("book") Book book);

    void delete(Review review);
}
