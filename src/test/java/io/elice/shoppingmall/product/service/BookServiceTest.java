package io.elice.shoppingmall.product.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.elice.shoppingmall.category.entity.Category;
import io.elice.shoppingmall.category.repository.CategoryRepository;
import io.elice.shoppingmall.category.service.CategoryService;
import io.elice.shoppingmall.product.dto.BookDTO;
import io.elice.shoppingmall.product.entity.Author;
import io.elice.shoppingmall.product.entity.Book;
import io.elice.shoppingmall.product.exception.BookNotFoundException;
import io.elice.shoppingmall.product.exception.CategoryNotFoundException;
import io.elice.shoppingmall.product.exception.NoSearchResultException;
import io.elice.shoppingmall.product.mapper.BookMapper;
import io.elice.shoppingmall.product.repository.AuthorRepository;
import io.elice.shoppingmall.product.repository.BookRepository;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorService authorService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookService bookService;

    private Book book;
    private BookDTO bookDTO;
    private Category category;
    private Author author;

    private final String TITLE = "Title";

    @BeforeEach
    public void setUp() {

        category = new Category();
        category.setName("Category");
        category.setId(1L);

        author = new Author();
        author.setName("Author");

        book = new Book();
        book.setId(1L);
        book.setTitle(TITLE);
        book.setCategory(category);
        book.setAuthor(author);

        bookDTO = new BookDTO();
        bookDTO.setId(1L);
        bookDTO.setTitle(TITLE);
        bookDTO.setCategory(category);
        bookDTO.setAuthor(author);
    }

    @Test
    public void testSaveBook() {
        when(bookMapper.toBookEntity(bookDTO)).thenReturn(book);
        when(authorService.searchAuthorByName(author.getName())).thenReturn(Optional.of(author));
        when(categoryService.searchCategoryByName(category.getName())).thenReturn(Optional.of(category));
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toBookDTO(book)).thenReturn(bookDTO);

        BookDTO savedBookDTO = bookService.saveBook(bookDTO);

        assertNotNull(savedBookDTO);
        assertEquals(TITLE,savedBookDTO.getTitle());
    }

    @Test
    public void testSaveBook_CategoryNotFoundException() {
        when(bookMapper.toBookEntity(bookDTO)).thenReturn(book);
        when(authorService.searchAuthorByName(author.getName())).thenReturn(Optional.of(author));
        when(categoryService.searchCategoryByName(category.getName())).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> {
            bookService.saveBook(bookDTO);
        });
    }

    @Test
    public void testGetAllBooks() {
        when(bookRepository.findAll()).thenReturn(Arrays.asList(book));
        when(bookMapper.toBookDTO(book)).thenReturn(bookDTO);

        List<BookDTO> bookDTOList = bookService.getAllBooks();

        assertNotNull(bookDTOList);
        assertEquals(1, bookDTOList.size());
        assertEquals(TITLE, bookDTOList.get(0).getTitle());
    }

    @Test
    public void testSearchBookById() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookMapper.toBookDTO(book)).thenReturn(bookDTO);

        BookDTO foundBookDTO = bookService.searchBookById(1L);

        assertNotNull(foundBookDTO);
        assertEquals(TITLE, foundBookDTO.getTitle());
    }

    @Test
    public void testSearchBookById_BookNotFoundException() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> {
            bookService.searchBookById(1L);
        });
    }

    @Test
    public void testSearchBooks() {
        when(bookRepository.findByTitleOrAuthor(TITLE)).thenReturn(Arrays.asList(book));
        when(bookMapper.toBookDTO(book)).thenReturn(bookDTO);

        List<BookDTO> bookDTOList = bookService.searchBooks(TITLE);

        assertNotNull(bookDTOList);
        assertEquals(1, bookDTOList.size());
        assertEquals(TITLE, bookDTOList.get(0).getTitle());
    }

    @Test
    public void testSearchBooks_NoSearchResultException() {
        when(bookRepository.findByTitleOrAuthor("예외")).thenReturn(Arrays.asList());

        assertThrows(NoSearchResultException.class, () -> {
            bookService.searchBooks("예외");
        });
    }

    @Test
    public void testGetBooksByCategory() {
        when(bookRepository.findByCategoryId(1L)).thenReturn(Arrays.asList(book));
        when(bookMapper.toBookDTO(book)).thenReturn(bookDTO);

        List<BookDTO> bookDTOList = bookService.getBooksByCategory(1L);

        assertNotNull(bookDTOList);
        assertEquals(1, bookDTOList.size());
        assertEquals(TITLE, bookDTOList.get(0).getTitle());
    }

    @Test
    public void testGetBooksByCategory_NoSearchResultException() {
        when(bookRepository.findByCategoryId(1L)).thenReturn(Arrays.asList());

        assertThrows(NoSearchResultException.class, () -> {
            bookService.getBooksByCategory(1L);
        });
    }

    @Test
    public void testRemoveBook() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.removeBook(1L);

        verify(bookRepository, times(1)).delete(book);
    }
}
