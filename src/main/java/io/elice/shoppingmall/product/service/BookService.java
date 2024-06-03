package io.elice.shoppingmall.product.service;

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
    private final BookMapper bookMapper;

    @Autowired
    public BookService(BookRepository bookRepository, AuthorService authorService, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
        this.bookMapper = bookMapper;
    }

    public BookDTO saveBook(BookDTO bookDTO) {
        Book book = bookMapper.toBookEntity(bookDTO);
        setAuthor(book);

        Book savedBook = bookRepository.save(book);
        return bookMapper.toBookDTO(savedBook);
    }

    public BookDTO modifyBookInfo(BookDTO bookDTO) {
        Book book = bookMapper.toBookEntity(bookDTO);
        book.setId(bookDTO.getId());
        setAuthor(book);

        Book updatedBook = bookRepository.save(book);
        return bookMapper.toBookDTO(updatedBook);
    }

    //동명의 작가를 생일로 구분하는 메서드
    private void setAuthor(Book book) {
        Author author = book.getAuthor();
        List<Author> authorList = authorService.searchAuthorByName(author.getName());

        if (authorList != null && !authorList.isEmpty()) {
            for (int i = 0; i < authorList.size(); i++) {
                Author searchedAuthor = authorList.get(i);
                if (searchedAuthor.getBirthDate().equals(author.getBirthDate())) {
                    book.setAuthor(searchedAuthor);
                }
            }
        }
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
