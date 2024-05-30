package io.elice.shoppingmall.product.repository;

import io.elice.shoppingmall.product.entity.Book;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Book save(Book book);

    List<Book> findAll();
}
