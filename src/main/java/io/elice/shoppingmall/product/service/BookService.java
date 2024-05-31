package io.elice.shoppingmall.product.service;

import io.elice.shoppingmall.product.dto.BookDTO;
import io.elice.shoppingmall.product.entity.Author;
import io.elice.shoppingmall.product.entity.Book;
import io.elice.shoppingmall.product.mapper.BookMapper;
import io.elice.shoppingmall.product.repository.AuthorRepository;
import io.elice.shoppingmall.product.repository.BookRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorService authorService;
    private final BookMapper bookMapper;

    @Autowired
    public BookService(BookRepository bookRepository, AuthorService authorService, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
        this.bookMapper = bookMapper;
    }

    public BookDTO saveBook(BookDTO bookDTO) {
        Book book = bookMapper.toBookEntity(bookDTO);
        Author author = book.getAuthor();
        List<Author> authorList = authorService.searchAuthorByName(author.getName());

        //동명이인을 구분하기 위한 메서드
        if (authorList != null && !authorList.isEmpty()) {
            for (int i = 0; i < authorList.size(); i++) {
                Author searchedAuthor = authorList.get(i);
                if (searchedAuthor.getBirthDate().equals(author.getBirthDate())) {
                    book.setAuthor(searchedAuthor);
                }
            }
        }

        Book savedBook = bookRepository.save(book);
        return bookMapper.toBookDTO(savedBook);
    }

    public BookDTO moidfyBookInfo(BookDTO bookDTO) {
        Book book = bookMapper.toBookEntity(bookDTO);
        book.setId(bookDTO.getId());
        Author author = book.getAuthor();
        List<Author> authorList = authorService.searchAuthorByName(author.getName());

        //동명이인을 구분하기 위한 메서드
        if (authorList != null && !authorList.isEmpty()) {
            for (int i = 0; i < authorList.size(); i++) {
                Author searchedAuthor = authorList.get(i);
                if (searchedAuthor.getBirthDate().equals(author.getBirthDate())) {
                    book.setAuthor(searchedAuthor);
                }
            }
        }

        Book updatedBook = bookRepository.save(book);
        return bookMapper.toBookDTO(updatedBook);
    }

    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toBookDTO)
                .collect(Collectors.toList());
    }

    public BookDTO searchBookById(Long id) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Book id is not exist : " + id));
        return bookMapper.toBookDTO(book);
    }
}
