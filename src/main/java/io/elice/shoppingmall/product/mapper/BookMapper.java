package io.elice.shoppingmall.product.mapper;

import io.elice.shoppingmall.product.dto.BookDTO;
import io.elice.shoppingmall.product.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BookMapper {

    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    @Mapping(source = "author", target = "author")
    @Mapping(source = "category", target = "category")
    Book toBookEntity(BookDTO bookDTO);

    @Mapping(source = "author", target = "author")
    @Mapping(source = "category", target = "category")
    BookDTO toBookDTO(Book book);
}
