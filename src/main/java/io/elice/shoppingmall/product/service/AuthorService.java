package io.elice.shoppingmall.product.service;

import io.elice.shoppingmall.product.entity.Author;
import io.elice.shoppingmall.product.repository.AuthorRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    @Autowired
    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Optional<Author> searchAuthorByName(String name) {
//        Optional<Author> author = authorRepository.findByName(name);
//        if (!author.isPresent()) {
//            throw new AuthorNotFoundException("Author: " + name + " not found");
//        }
//        return author;
        return authorRepository.findByName(name);
    }
}
