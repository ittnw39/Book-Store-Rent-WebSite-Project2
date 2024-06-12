package io.elice.shoppingmall.product.controller;

import io.elice.shoppingmall.product.dto.BookDTO;
import io.elice.shoppingmall.product.dto.ReviewDTO;
import io.elice.shoppingmall.product.service.BookService;
import io.elice.shoppingmall.product.service.ReviewService;
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
    private final ReviewService reviewService;
    private final BookMapper bookMapper;

    @Autowired
    public ProductController(BookService bookService, ReviewService reviewService, BookMapper bookMapper) {
        this.bookService = bookService;
        this.reviewService = reviewService;
        this.bookMapper = bookMapper;
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


    //상품 리뷰 목록 조회(정렬 포함)
    @GetMapping("/book/{bookId}/reviews")
    public ResponseEntity<List<ReviewDTO>> getReviewsByBook(@PathVariable("bookId") Long bookId, @RequestParam(required = false) String sortBy) {
        BookDTO bookDTO = bookService.searchBookById(bookId);
        List<ReviewDTO> reviews = reviewService.getReviewsByBookSorted(bookMapper.toBookEntity(bookDTO), sortBy);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    //상품 리뷰 작성
    @PostMapping("/book/review/{bookId}")
    public ResponseEntity<ReviewDTO> createReview(@PathVariable("bookId") Long bookId, @RequestBody ReviewDTO reviewDTO,
                                                  Principal principal) {
        if(principal == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        // 이메일로 사용자 ID를 찾음
        String email = principal.getName();
        BookDTO bookDTO = bookService.searchBookById(bookId);
        reviewDTO.setBookDTO(bookDTO);
        ReviewDTO newReview = reviewService.saveReview(reviewDTO, email);
        return new ResponseEntity<>(newReview, HttpStatus.CREATED);

    }

    //상품 리뷰 수정
    @PutMapping("/book/review/{reviewId}")
    public ResponseEntity<ReviewDTO> updateReview(@PathVariable("reviewId") Long reviewId, @RequestBody ReviewDTO reviewDTO, Principal principal) {
        String email = principal.getName();

        reviewService.verifyReviewOwner(reviewId, email);

        reviewDTO.setId(reviewId);
        ReviewDTO updatedReview = reviewService.modifyReview(reviewDTO, email);
        return new ResponseEntity<>(updatedReview, HttpStatus.OK);
    }

    //상품 리뷰 삭제
    @DeleteMapping("/book/review/{reviewId}")
    public ResponseEntity<ReviewDTO> deleteReview(@PathVariable("reviewId") Long reviewId, Principal principal) {
        String email = principal.getName();

        reviewService.verifyReviewOwner(reviewId, email);

        reviewService.removeReview(reviewId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //상품 리뷰 좋아요 기능
    @PostMapping("/book/review/{reviewId}/like")
    public ResponseEntity<ReviewDTO> likeReview(@PathVariable("reviewId") Long reviewId) {
        ReviewDTO likedReview = reviewService.addLikeReview(reviewId);
        return new ResponseEntity<>(likedReview, HttpStatus.OK);
    }
}
