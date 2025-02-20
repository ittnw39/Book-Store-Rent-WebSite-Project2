package io.elice.shoppingmall.product.mapper;

import io.elice.shoppingmall.product.dto.ReviewDTO;
import io.elice.shoppingmall.product.entity.Review;
import io.elice.shoppingmall.user.dto.UserDTO;
import io.elice.shoppingmall.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ReviewMapper {

    ReviewMapper INSTANCE = Mappers.getMapper(ReviewMapper.class);

    @Mapping(source = "bookDTO", target = "book")
    @Mapping(source = "userDTO", target = "user")
    Review toReviewEntity(ReviewDTO reviewDTO);

    @Mapping(source = "book", target = "bookDTO")
    @Mapping(source = "user", target = "userDTO")
    ReviewDTO toReviewDTO(Review review);

    User toUserEntity(UserDTO userDTO);

    UserDTO toUserDTO(User user);
}
