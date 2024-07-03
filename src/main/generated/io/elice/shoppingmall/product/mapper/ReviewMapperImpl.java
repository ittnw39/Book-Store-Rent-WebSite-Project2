package io.elice.shoppingmall.product.mapper;

import io.elice.shoppingmall.product.dto.BookDTO;
import io.elice.shoppingmall.product.dto.ReviewDTO;
import io.elice.shoppingmall.product.entity.Book;
import io.elice.shoppingmall.product.entity.Review;
import io.elice.shoppingmall.user.dto.UserDTO;
import io.elice.shoppingmall.user.entity.User;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-07-01T16:15:15+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.10 (Oracle Corporation)"
)
public class ReviewMapperImpl implements ReviewMapper {

    @Override
    public Review toReviewEntity(ReviewDTO reviewDTO) {
        if ( reviewDTO == null ) {
            return null;
        }

        Review review = new Review();

        review.setBook( bookDTOToBook( reviewDTO.getBookDTO() ) );
        review.setUser( toUserEntity( reviewDTO.getUserDTO() ) );
        review.setId( reviewDTO.getId() );
        review.setComment( reviewDTO.getComment() );
        review.setCreatedAt( reviewDTO.getCreatedAt() );
        review.setLikes( reviewDTO.getLikes() );

        return review;
    }

    @Override
    public ReviewDTO toReviewDTO(Review review) {
        if ( review == null ) {
            return null;
        }

        ReviewDTO reviewDTO = new ReviewDTO();

        reviewDTO.setBookDTO( bookToBookDTO( review.getBook() ) );
        reviewDTO.setUserDTO( toUserDTO( review.getUser() ) );
        reviewDTO.setId( review.getId() );
        reviewDTO.setComment( review.getComment() );
        reviewDTO.setCreatedAt( review.getCreatedAt() );
        reviewDTO.setLikes( review.getLikes() );

        return reviewDTO;
    }

    @Override
    public User toUserEntity(UserDTO userDTO) {
        if ( userDTO == null ) {
            return null;
        }

        User user = new User();

        user.setId( userDTO.getId() );
        user.setEmail( userDTO.getEmail() );
        user.setUsername( userDTO.getUsername() );
        user.setPassword( userDTO.getPassword() );
        user.setPhone_number( userDTO.getPhone_number() );
        user.setAddress( userDTO.getAddress() );
        user.setNickname( userDTO.getNickname() );
        user.setAdmin( userDTO.isAdmin() );
        user.setTotalSpent( userDTO.getTotalSpent() );
        user.setCreatedAt( userDTO.getCreatedAt() );

        return user;
    }

    @Override
    public UserDTO toUserDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UserDTO userDTO = new UserDTO();

        userDTO.setId( user.getId() );
        userDTO.setEmail( user.getEmail() );
        userDTO.setUsername( user.getUsername() );
        userDTO.setPhone_number( user.getPhone_number() );
        userDTO.setAddress( user.getAddress() );
        userDTO.setNickname( user.getNickname() );
        userDTO.setTotalSpent( user.getTotalSpent() );
        userDTO.setPassword( user.getPassword() );
        userDTO.setAdmin( user.isAdmin() );
        userDTO.setCreatedAt( user.getCreatedAt() );

        return userDTO;
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
