package io.elice.shoppingmall.product.service;

import io.elice.shoppingmall.category.entity.Category;
import io.elice.shoppingmall.category.service.CategoryService;
import io.elice.shoppingmall.product.dto.BookDTO;
import io.elice.shoppingmall.product.entity.Author;
import io.elice.shoppingmall.product.entity.Book;
import io.elice.shoppingmall.product.mapper.BookMapper;
import io.elice.shoppingmall.product.repository.BookRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorService authorService;
    private final CategoryService categoryService;
    private final BookMapper bookMapper;

    @Autowired
    public BookService(BookRepository bookRepository, AuthorService authorService, CategoryService categoryService, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
        this.categoryService = categoryService;
        this.bookMapper = bookMapper;
    }

    public BookDTO saveBook(BookDTO bookDTO) {
        Book book = bookMapper.toBookEntity(bookDTO);

        Author author = authorService.searchAuthorByName(book.getAuthor().getName()).orElse(null);
        if (author != null) {
            book.setAuthor(author);
        }

        Category category = categoryService.searchCategoryByName(book.getCategory().getName()).orElse(null);
        if (category != null) {
            book.setCategory(category);
        }

        Book savedBook = bookRepository.save(book);
        return bookMapper.toBookDTO(savedBook);
    }

    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toBookDTO)
                .collect(Collectors.toList());
    }

    public BookDTO searchBookById(Long id) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Book id is not exists : " + id));
        return bookMapper.toBookDTO(book);
    }

    public void removeBook(Long id) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Book id is not exists : " + id));
        bookRepository.delete(book);
    }
}
