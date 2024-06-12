package io.elice.shoppingmall.product.repository;

import io.elice.shoppingmall.product.entity.Book;
import java.util.List;
import java.util.Optional;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Book save(Book book);

    List<Book> findAll();

    Optional<Book> findById(Long id);

    void delete(Book book);

    List<Book> findByCategoryId(Long CategoryId);

    //책 제목, 저자명 부분 검색 가능
    @Query("SELECT b FROM Book b WHERE b.title LIKE %:keyword% OR b.author.name LIKE %:keyword%")
    List<Book> findByTitleOrAuthor(@Param("keyword") String keyword);
}
