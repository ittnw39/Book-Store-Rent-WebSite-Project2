package io.elice.shoppingmall.product.service;

import io.elice.shoppingmall.product.dto.BookDTO;
import io.elice.shoppingmall.product.dto.ReviewDTO;
import io.elice.shoppingmall.product.entity.Book;
import io.elice.shoppingmall.product.entity.Review;
import io.elice.shoppingmall.product.mapper.ReviewMapper;
import io.elice.shoppingmall.product.repository.ReviewRepository;
import io.elice.shoppingmall.user.entity.User;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
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
        reviewRepository.save(review);
        return reviewMapper.toReviewDTO(review);
    }

    public ReviewDTO modifyReview(ReviewDTO reviewDTO) {
        ReviewDTO updatedReview = searchReviewById(reviewDTO.getId());
        reviewDTO.setBookDTO(updatedReview.getBookDTO());
        Review review = reviewMapper.toReviewEntity(reviewDTO);
        reviewRepository.save(review);
        return reviewMapper.toReviewDTO(review);
    }

    public ReviewDTO searchReviewById(Long id) {
        Review review = reviewRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Review id is not exists : " + id));
        return reviewMapper.toReviewDTO(review);
    }

    public List<ReviewDTO> getReviewsByBook(Book book) {
        return reviewRepository.findByBook(book).stream()
                .map(reviewMapper::toReviewDTO)
                .collect(Collectors.toList());
    }

    public List<ReviewDTO> getReviewsByUser(User user) {
        return reviewRepository.findByUser(user).stream()
                .map(reviewMapper::toReviewDTO)
                .collect(Collectors.toList());
    }

    public void removeReview(Long id) {
        Review review = reviewRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Review id is not exists : " + id));
        reviewRepository.delete(review);
    }
}
