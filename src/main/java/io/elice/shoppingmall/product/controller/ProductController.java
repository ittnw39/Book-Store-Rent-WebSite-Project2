package io.elice.shoppingmall.product.controller;

import io.elice.shoppingmall.product.dto.BookDTO;
import io.elice.shoppingmall.product.service.BookService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ProductController {

    private final BookService bookService;

    @Autowired
    public ProductController(BookService bookService) {
        this.bookService = bookService;
    }

    //상품 목록 조회 페이지
    @GetMapping("/books")
    public ResponseEntity<List<BookDTO>> viewBookList() {
        List<BookDTO> bookList = bookService.getAllBooks();
        return new ResponseEntity<>(bookList, HttpStatus.OK);
    }

    //상품 상세 조회 페이지
    @GetMapping("/book/{bookId}")
    public ResponseEntity<BookDTO> viewBook(@PathVariable("bookId") Long id) {
        BookDTO bookDTO = bookService.searchBookById(id);
        return new ResponseEntity<>(bookDTO, HttpStatus.OK);
    }
}
