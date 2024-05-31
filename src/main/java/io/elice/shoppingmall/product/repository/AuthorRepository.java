package io.elice.shoppingmall.product.repository;

import io.elice.shoppingmall.product.entity.Author;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {


    List<Author> findByName(String name);
}
