package io.elice.shoppingmall.order.mapper;

import io.elice.shoppingmall.order.DTO.OrderDTO;
import io.elice.shoppingmall.order.DTO.OrderLineBookDTO;
import io.elice.shoppingmall.order.DTO.OrderLineDTO;
import io.elice.shoppingmall.order.entity.OrderLine;
import io.elice.shoppingmall.order.entity.OrderLineBook;
import io.elice.shoppingmall.order.entity.Orders;
import io.elice.shoppingmall.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(source = "orderDTO.userId", target = "user.id")
    @Mapping(source = "orderDTO.id", target = "id")
    @Mapping(target = "orderLine", source = "orderDTO.orderLines")
    Orders toOrderEntity(OrderDTO orderDTO, User user);

    @Mapping(target = "orderLines", source = "order.orderLine")
    @Mapping(source = "user.id", target = "userId")
    OrderDTO toOrderDTO(Orders order);

    @Mapping(source = "orderId", target = "orders.id")
    OrderLine toOrderLineEntity(OrderLineDTO orderLineDTO);

    @Mapping(source = "orders.id", target = "orderId")
    OrderLineDTO toOrderLineDTO(OrderLine orderLine);

    @Mapping(source = "bookId", target = "book.id")
    @Mapping(source = "orderLineId", target = "orderLine.id")
    OrderLineBook toOrderLineBookEntity(OrderLineBookDTO orderLineBookDTO);

    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "orderLine.id", target = "orderLineId")
    OrderLineBookDTO toOrderLineBookDTO(OrderLineBook orderLineBook);

    @Mapping(source = "orderDTO.userId", target = "user.id")
    @Mapping(source = "orderDTO.orderLines", target = "orderLine")
    void updateOrderFromDTO(OrderDTO orderDTO, @MappingTarget Orders order);

    @Mapping(source = "orderLineDTO.orderId", target = "orders.id")
    void updateOrderLineFromDTO(OrderLineDTO orderLineDTO, @MappingTarget OrderLine orderLine);

    default User map(Long userId) {
        if (userId == null) {
            return null;
        }
        User user = new User();
        user.setId(userId);
        return user;
    }
}
