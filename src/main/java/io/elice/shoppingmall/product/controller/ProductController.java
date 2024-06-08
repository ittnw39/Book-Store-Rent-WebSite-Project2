package io.elice.shoppingmall.product.controller;

import io.elice.shoppingmall.product.dto.BookDTO;
import io.elice.shoppingmall.product.dto.ReviewDTO;
import io.elice.shoppingmall.product.service.BookService;
import io.elice.shoppingmall.product.service.ReviewService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ProductController {

    private final BookService bookService;
    private final ReviewService reviewService;

    @Autowired
    public ProductController(BookService bookService, ReviewService reviewService) {
        this.bookService = bookService;
        this.reviewService = reviewService;
    }

    //상품 목록 조회 페이지
    @GetMapping("/books")
    public ResponseEntity<List<BookDTO>> viewBookList() {
        List<BookDTO> bookList = bookService.getAllBooks();
        return new ResponseEntity<>(bookList, HttpStatus.OK);
    }

    //카테고리별 상품 목록 조회 페이지
    @GetMapping("/books/category")
    public ResponseEntity<List<BookDTO>> viewBookListByCategory(@RequestParam Long categoryId) {
        List<BookDTO> categoryBookList = bookService.getBooksByCategory(categoryId);
        return new ResponseEntity<>(categoryBookList, HttpStatus.OK);
    }

    //상품 검색 기능 추가 (책 제목, 저자)
    @GetMapping("/books/search")
    public ResponseEntity<List<BookDTO>> searchBookList(@RequestParam String keyword) {
        List<BookDTO> searchResults = bookService.searchBooks(keyword);
        return new ResponseEntity<>(searchResults, HttpStatus.OK);
    }

    //상품 상세 조회 페이지
    @GetMapping("/book/{bookId}")
    public ResponseEntity<BookDTO> viewBook(@PathVariable("bookId") Long id) {
        BookDTO bookDTO = bookService.searchBookById(id);
        return new ResponseEntity<>(bookDTO, HttpStatus.OK);
    }

    //상품 리뷰 작성
    @PostMapping("/book/review/{bookId}")
    public ResponseEntity<ReviewDTO> createReview(@PathVariable("bookId") Long id, @RequestBody ReviewDTO reviewDTO) {
        BookDTO bookDTO = bookService.searchBookById(id);

        reviewDTO.setBookDTO(bookDTO);

        ReviewDTO newReview = reviewService.saveReview(reviewDTO);
        return new ResponseEntity<>(newReview, HttpStatus.CREATED);
    }

    //상품 리뷰 수정
    @PutMapping("/book/review/{reviewId}")
    public ResponseEntity<ReviewDTO> updateReview(@PathVariable("reviewId") Long id, @RequestBody ReviewDTO reviewDTO) {
        reviewDTO.setId(id);
        ReviewDTO selectedReview = reviewService.modifyReview(reviewDTO);
        return new ResponseEntity<>(selectedReview, HttpStatus.OK);
    }

    //상품 리뷰 삭제
    @DeleteMapping("/book/review/{reviewId}")
    public ResponseEntity<ReviewDTO> deleteReview(@PathVariable("reviewId") Long id) {
        reviewService.removeReview(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
