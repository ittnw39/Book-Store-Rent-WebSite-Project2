package io.elice.shoppingmall.product.controller;

import io.elice.shoppingmall.product.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByCategoryId(Long CategoryId);
}
