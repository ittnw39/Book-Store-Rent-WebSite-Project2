package io.elice.shoppingmall.product.mapper;

import io.elice.shoppingmall.product.dto.BookDTO;
import io.elice.shoppingmall.product.dto.ReviewDTO;
import io.elice.shoppingmall.product.entity.Book;
import io.elice.shoppingmall.product.entity.Review;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-06-06T17:42:28+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.10 (Azul Systems, Inc.)"
)
public class ReviewMapperImpl implements ReviewMapper {

    @Override
    public Review toReviewEntity(ReviewDTO reviewDTO) {
        if ( reviewDTO == null ) {
            return null;
        }

        Review review = new Review();

        review.setBook( bookDTOToBook( reviewDTO.getBookDTO() ) );
        review.setId( reviewDTO.getId() );
        review.setRating( reviewDTO.getRating() );
        review.setComment( reviewDTO.getComment() );

        return review;
    }

    @Override
    public ReviewDTO toReviewDTO(Review review) {
        if ( review == null ) {
            return null;
        }

        ReviewDTO reviewDTO = new ReviewDTO();

        reviewDTO.setBookDTO( bookToBookDTO( review.getBook() ) );
        reviewDTO.setId( review.getId() );
        reviewDTO.setComment( review.getComment() );
        reviewDTO.setRating( review.getRating() );

        return reviewDTO;
    }

    protected Book bookDTOToBook(BookDTO bookDTO) {
        if ( bookDTO == null ) {
            return null;
        }

        Book book = new Book();

        book.setId( bookDTO.getId() );
        book.setTitle( bookDTO.getTitle() );
        book.setPublisher( bookDTO.getPublisher() );
        book.setPublishedDate( bookDTO.getPublishedDate() );
        book.setPrice( bookDTO.getPrice() );
        book.setDescription( bookDTO.getDescription() );
        book.setImageURL( bookDTO.getImageURL() );
        book.setTotalStockQuantity( bookDTO.getTotalStockQuantity() );
        book.setPage( bookDTO.getPage() );
        book.setAuthor( bookDTO.getAuthor() );
        book.setCategory( bookDTO.getCategory() );

        return book;
    }

    protected BookDTO bookToBookDTO(Book book) {
        if ( book == null ) {
            return null;
        }

        BookDTO bookDTO = new BookDTO();

        bookDTO.setId( book.getId() );
        bookDTO.setTitle( book.getTitle() );
        bookDTO.setPublisher( book.getPublisher() );
        bookDTO.setPublishedDate( book.getPublishedDate() );
        bookDTO.setPrice( book.getPrice() );
        bookDTO.setDescription( book.getDescription() );
        bookDTO.setImageURL( book.getImageURL() );
        bookDTO.setTotalStockQuantity( book.getTotalStockQuantity() );
        bookDTO.setPage( book.getPage() );
        bookDTO.setAuthor( book.getAuthor() );
        bookDTO.setCategory( book.getCategory() );

        return bookDTO;
    }
}
