package io.elice.shoppingmall.product.service;

import io.elice.shoppingmall.product.dto.ReviewDTO;
import io.elice.shoppingmall.product.entity.Book;
import io.elice.shoppingmall.product.entity.Review;
import io.elice.shoppingmall.product.mapper.ReviewMapper;
import io.elice.shoppingmall.product.repository.ReviewRepository;
import io.elice.shoppingmall.user.entity.User;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, ReviewMapper reviewMapper) {
        this.reviewRepository = reviewRepository;
        this.reviewMapper = reviewMapper;
    }

    public ReviewDTO saveReview(ReviewDTO reviewDTO) {
        Review review = reviewMapper.toReviewEntity(reviewDTO);

        review = reviewRepository.save(review);
        return reviewMapper.toReviewDTO(review);
    }

    public Review searchReviewById(Long id) {
        Review review = reviewRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Review id is not exists : " + id));
        return review;
    }

    public List<Review> getReviewsByBook(Book book) {
        return reviewRepository.findByBook(book);
    }

    public List<Review> getReviewsByUser(User user) {
        return reviewRepository.findByUser(user);
    }

    public void removeReview(Long id) {
        Review review = reviewRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Review id is not exists : " + id));
        reviewRepository.delete(review);
    }
}
