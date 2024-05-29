package io.elice.shoppingmall.order.mapper;

import io.elice.shoppingmall.order.DTO.OrderDTO;
import io.elice.shoppingmall.order.DTO.OrderLineBookDTO;
import io.elice.shoppingmall.order.DTO.OrderLineDTO;
import io.elice.shoppingmall.order.entity.OrderLine;
import io.elice.shoppingmall.order.entity.OrderLineBook;
import io.elice.shoppingmall.order.entity.Orders;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.mapstruct.Mapping;

@Mapper
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    Orders toOrderEntity(OrderDTO orderDTO);
    OrderDTO toOrderDTO(Orders order);

    OrderLine toOrderLineEntity(OrderLineDTO orderLineDTO);
    OrderLineDTO toOrderLineDTO(OrderLine orderLine);

    @Mapping(source = "bookId", target = "book.id")
    @Mapping(source = "orderLineId", target = "orderLine.id")
    OrderLineBook toOrderLineBookEntity(OrderLineBookDTO orderLineBookDTO);

    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "orderLine.id", target = "orderLineId")
    OrderLineBookDTO toOrderLineBookDTO(OrderLineBook orderLineBook);
}
