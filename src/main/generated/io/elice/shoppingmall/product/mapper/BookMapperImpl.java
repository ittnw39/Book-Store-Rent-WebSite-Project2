package io.elice.shoppingmall.product.mapper;

import io.elice.shoppingmall.product.dto.BookDTO;
import io.elice.shoppingmall.product.entity.Book;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-05-31T10:51:58+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.1 (Oracle Corporation)"
)
public class BookMapperImpl implements BookMapper {

    @Override
    public Book toBookEntity(BookDTO bookDTO) {
        if ( bookDTO == null ) {
            return null;
        }

        Book book = new Book();

        book.setAuthor( bookDTO.getAuthor() );
        book.setCategory( bookDTO.getCategory() );
        book.setId( bookDTO.getId() );
        book.setTitle( bookDTO.getTitle() );
        book.setPublisher( bookDTO.getPublisher() );
        book.setPublishedDate( bookDTO.getPublishedDate() );
        book.setPrice( bookDTO.getPrice() );
        book.setDescription( bookDTO.getDescription() );
        book.setImageURL( bookDTO.getImageURL() );
        book.setTotalStockQuantity( bookDTO.getTotalStockQuantity() );
        book.setPage( bookDTO.getPage() );

        return book;
    }

    @Override
    public BookDTO toBookDTO(Book book) {
        if ( book == null ) {
            return null;
        }

        BookDTO bookDTO = new BookDTO();

        bookDTO.setAuthor( book.getAuthor() );
        bookDTO.setCategory( book.getCategory() );
        bookDTO.setId( book.getId() );
        bookDTO.setTitle( book.getTitle() );
        bookDTO.setPublisher( book.getPublisher() );
        bookDTO.setPublishedDate( book.getPublishedDate() );
        bookDTO.setPrice( book.getPrice() );
        bookDTO.setDescription( book.getDescription() );
        bookDTO.setImageURL( book.getImageURL() );
        bookDTO.setTotalStockQuantity( book.getTotalStockQuantity() );
        bookDTO.setPage( book.getPage() );

        return bookDTO;
    }
}
