package io.elice.shoppingmall.product.controller;

import io.elice.shoppingmall.product.dto.BookDTO;
import io.elice.shoppingmall.product.dto.ReviewDTO;
import io.elice.shoppingmall.product.service.BookService;
import io.elice.shoppingmall.product.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/book/review")
public class ReviewController {

    private final BookService bookService;
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(BookService bookService, ReviewService reviewService) {
        this.bookService = bookService;
        this.reviewService = reviewService;
    }

    //상품 리뷰 작성
    @PostMapping("/{bookId}")
    public ResponseEntity<ReviewDTO> createReview(@PathVariable("bookId") Long id, @RequestBody ReviewDTO reviewDTO) {
        BookDTO bookDTO = bookService.searchBookById(id);

        reviewDTO.setBookDTO(bookDTO);

        ReviewDTO newReview = reviewService.saveReview(reviewDTO);
        return new ResponseEntity<>(newReview, HttpStatus.CREATED);
    }

    //상품 리뷰 수정
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewDTO> updateReview(@PathVariable("reviewId") Long id, @RequestBody ReviewDTO reviewDTO) {
        reviewDTO.setId(id);
        ReviewDTO selectedReview = reviewService.modifyReview(reviewDTO);
        return new ResponseEntity<>(selectedReview, HttpStatus.OK);
    }

    //상품 리뷰 삭제
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ReviewDTO> deleteReview(@PathVariable("reviewId") Long id) {
        reviewService.removeReview(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
