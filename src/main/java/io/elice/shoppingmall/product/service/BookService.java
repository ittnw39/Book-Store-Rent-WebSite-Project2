package io.elice.shoppingmall.product.service;

import io.elice.shoppingmall.category.entity.Category;
import io.elice.shoppingmall.category.service.CategoryService;
import io.elice.shoppingmall.product.dto.BookDTO;
import io.elice.shoppingmall.product.entity.Author;
import io.elice.shoppingmall.product.entity.Book;
import io.elice.shoppingmall.product.exception.BookNotFoundException;
import io.elice.shoppingmall.product.exception.CategoryNotFoundException;
import io.elice.shoppingmall.product.exception.NoSearchResultException;
import io.elice.shoppingmall.product.mapper.BookMapper;
import io.elice.shoppingmall.product.repository.BookRepository;
import java.net.MalformedURLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorService authorService;
    private final CategoryService categoryService;
    private final BookMapper bookMapper;
    private S3Service s3Service;

    @Autowired
    public BookService(BookRepository bookRepository, AuthorService authorService, CategoryService categoryService, BookMapper bookMapper, S3Service s3Service) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
        this.categoryService = categoryService;
        this.bookMapper = bookMapper;
        this.s3Service = s3Service;
    }

    @Transactional
    public BookDTO saveBook(BookDTO bookDTO) {
        Book book = bookMapper.toBookEntity(bookDTO);

        Author author = authorService.searchAuthorByName(book.getAuthor().getName()).orElse(null);
        if (author != null) {
            book.setAuthor(author);
        }

        Category category = categoryService.searchCategoryByName(book.getCategory().getName())
                                .orElseThrow(() -> new CategoryNotFoundException("Category not found: " + book.getCategory().getName()));
        book.setCategory(category);

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
                .orElseThrow(() -> new BookNotFoundException("Book id is not exists : " + id));
        return bookMapper.toBookDTO(book);
    }

    //책 검색 기능
    public List<BookDTO> searchBooks(String keyword) {
        List<Book> books = bookRepository.findByTitleOrAuthor(keyword);
        if (books.isEmpty()) {
            throw new NoSearchResultException();
        }
        return books.stream()
                .map(bookMapper::toBookDTO)
                .collect(Collectors.toList());
    }

    //카테고리별 목록
    public List<BookDTO> getBooksByCategory(Long categoryId) {
        List<Book> books = bookRepository.findByCategoryId(categoryId);
        if (books.isEmpty()) {
            throw new NoSearchResultException();
        }
        return books.stream()
                .map(bookMapper::toBookDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void removeBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Book id is not exists : " + id));

        if (book.getImageURL() != null) {
            String objectKey = s3Service.extractObjectKeyFromUrl(book.getImageURL());
            s3Service.delete(objectKey);
        }

        bookRepository.delete(book);
    }
}
