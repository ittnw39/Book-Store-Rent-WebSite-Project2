package io.elice.shoppingmall.product.service;

import io.elice.shoppingmall.product.dto.BookDTO;
import io.elice.shoppingmall.product.dto.ReviewDTO;
import io.elice.shoppingmall.product.entity.Book;
import io.elice.shoppingmall.product.entity.Review;
import io.elice.shoppingmall.product.mapper.ReviewMapper;
import io.elice.shoppingmall.product.repository.BookRepository;
import io.elice.shoppingmall.product.repository.ReviewRepository;
import io.elice.shoppingmall.user.entity.User;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import io.elice.shoppingmall.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final UserService userService;
    private final BookRepository bookRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, ReviewMapper reviewMapper, UserService userService, BookRepository bookRepository) {
        this.reviewRepository = reviewRepository;
        this.reviewMapper = reviewMapper;
        this.userService = userService;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public ReviewDTO saveReview(ReviewDTO reviewDTO, String email) {
            Review review = reviewMapper.toReviewEntity(reviewDTO);
            User user = userService.findUserByEmail(email);
            review.setUser(user);
            reviewRepository.save(review);
            updateReviewCount(review.getBook().getId()); // 리뷰 추가 시 리뷰 수 업데이트
            return reviewMapper.toReviewDTO(review);
    }

    @Transactional
    public ReviewDTO modifyReview(ReviewDTO reviewDTO, String email) {
        ReviewDTO updatedReview = searchReviewById(reviewDTO.getId());
        reviewDTO.setLikes(updatedReview.getLikes()); // 좋아요 수 유지
        reviewDTO.setBookDTO(updatedReview.getBookDTO());
        Review review = reviewMapper.toReviewEntity(reviewDTO);
        User user = userService.findUserByEmail(email);
        review.setUser(user);
        reviewRepository.save(review);
        return reviewMapper.toReviewDTO(review);
    }

    public ReviewDTO searchReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Review id is not exists : " + id));
        return reviewMapper.toReviewDTO(review);
    }

    public List<ReviewDTO> getReviewsByBookSorted(Book book, String sortBy) {
        List<Review> reviews;
        if ("likes".equalsIgnoreCase(sortBy)) {
            reviews = reviewRepository.findByBookOrderByLikesDesc(book);
            System.out.println("Sorting by likes");
        } else {
            reviews = reviewRepository.findByBookOrderByCreatedAtDesc(book);
            System.out.println("Sorting by date");
        }
        return reviews.stream()
                .map(reviewMapper::toReviewDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void removeReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NoSuchElementException("Review id is not exists : " + reviewId));
        reviewRepository.delete(review);
        updateReviewCount(review.getBook().getId());
    }

    public ReviewDTO addLikeReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NoSuchElementException("Review id is not exists : " + reviewId));
        review.setLikes(review.getLikes() + 1);
        reviewRepository.save(review);
        return reviewMapper.toReviewDTO(review);
    }

    public void verifyReviewOwner(Long reviewId, String email) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NoSuchElementException("Review id is not exists : " + reviewId));
        if (!review.getUser().getEmail().equals(email)) {
            throw new AccessDeniedException("You do not have permission to modify or delete this review");
        }
    }

    // 리뷰 수 카운트
    private void updateReviewCount(Long bookId) {
        int reviewCount = reviewRepository.countByBookId(bookId);
        Book book = bookRepository.findById(bookId).orElseThrow();
        book.setReviewCount(reviewCount);
        bookRepository.save(book);
    }
}
