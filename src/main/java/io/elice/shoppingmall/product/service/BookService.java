package io.elice.shoppingmall.product.service;

import io.elice.shoppingmall.product.dto.BookDTO;
import io.elice.shoppingmall.product.entity.Book;
import io.elice.shoppingmall.product.mapper.BookMapper;
import io.elice.shoppingmall.product.repository.BookRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Autowired
    public BookService(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    public BookDTO saveBook(BookDTO bookDTO) {
        Book book = bookMapper.toBookEntity(bookDTO);
        Book savedBook = bookRepository.save(book);
        return bookMapper.toBookDTO(savedBook);
    }

    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toBookDTO)
                .collect(Collectors.toList());
    }
}
