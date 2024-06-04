package io.elice.shoppingmall.order.mapper;


import io.elice.shoppingmall.order.DTO.OrderDTO;
import io.elice.shoppingmall.order.DTO.OrderLineBookDTO;
import io.elice.shoppingmall.order.DTO.OrderLineDTO;
import io.elice.shoppingmall.order.entity.OrderLine;
import io.elice.shoppingmall.order.entity.OrderLineBook;
import io.elice.shoppingmall.order.entity.Orders;

import javax.annotation.processing.Generated;

@Generated(
        value = "org.mapstruct.ap.MappingProcessor",
        date = "2024-06-04T10:51:58+0900",
        comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.1 (Oracle Corporation)"
)

public class OrderMapperImpl implements OrderMapper {

    @Override
    public Orders toOrderEntity(OrderDTO orderDTO) {
        if (orderDTO == null) {
            return null;
        }

        Orders orders = new Orders();

        orders.setId(orderDTO.getId());
        orders.setOrderDate(orderDTO.getOrderDate());
        orders.setOrderStatus(orderDTO.getOrderStatus());
        orders.setTotalAmount(orderDTO.getTotalAmount());
        orders.setDiscountRate(orderDTO.getDiscountRate());
        orders.setUserAddress(orderDTO.getUserAddress());
        orders.setOrderOption(orderDTO.getOrderOption());

        return orders;
    }

    @Override
    public OrderDTO toOrderDTO(Orders order) {
        if (order == null) {
            return null;
        }

        OrderDTO orderDTO = new OrderDTO();

        orderDTO.setId(order.getId());
        orderDTO.setOrderDate(order.getOrderDate());
        orderDTO.setOrderStatus(order.getOrderStatus());
        orderDTO.setTotalAmount(order.getTotalAmount());
        orderDTO.setDiscountRate(order.getDiscountRate());
        orderDTO.setUserAddress(order.getUserAddress());
        orderDTO.setOrderOption(order.getOrderOption());

        return orderDTO;
    }

    @Override
    public OrderLine toOrderLineEntity(OrderLineDTO orderLineDTO) {
        if (orderLineDTO == null) {
            return null;
        }

        OrderLine orderLine = new OrderLine();

        orderLine.setId(orderLineDTO.getId());
        orderLine.setQuantity(orderLineDTO.getQuantity());
        orderLine.setPrice(orderLineDTO.getPrice());
        orderLine.setDiscountRate(orderLineDTO.getDiscountRate());
        orderLine.setOrderId(orderLineDTO.getOrderId());
        orderLine.setOrderLineBooks(orderLineDTO.getOrderLineBooks());

        return orderLine;
    }

    @Override
    public OrderLineDTO toOrderLineDTO(OrderLine orderLine) {
        if (orderLine == null) {
            return null;
        }

        OrderLineDTO orderLineDTO = new OrderLineDTO();

        orderLineDTO.setId(orderLine.getId());
        orderLineDTO.setQuantity(orderLine.getQuantity());
        orderLineDTO.setPrice(orderLine.getPrice());
        orderLineDTO.setDiscountRate(orderLine.getDiscountRate());
        orderLineDTO.setOrderId(orderLine.getOrderId());
        orderLineDTO.setOrderLineBooks(orderLine.getOrderLineBooks());

        return orderLineDTO;
    }

    @Override
    public OrderLineBook toOrderLineBookEntity(OrderLineBookDTO orderLineBookDTO) {
        if (orderLineBookDTO == null) {
            return null;
        }

        OrderLineBook orderLineBook = new OrderLineBook();

        orderLineBook.setQuantity(orderLineBookDTO.getQuantity());
        orderLineBook.setBookId(orderLineBookDTO.getBookId());
        orderLineBook.setOrderLineId(orderLineBookDTO.getOrderLineId());

        return orderLineBook;
    }

    @Override
    public OrderLineBookDTO toOrderLineBookDTO(OrderLineBook orderLineBook) {
        if (orderLineBook == null) {
            return null;
        }

        OrderLineBookDTO orderLineBookDTO = new OrderLineBookDTO();

        orderLineBookDTO.setQuantity(orderLineBook.getQuantity());
        orderLineBookDTO.setBookId(orderLineBook.getBookId());
        orderLineBookDTO.setOrderLineId(orderLineBook.getOrderLineId());

        return orderLineBookDTO;
    }

    @Override
    public void updateOrderFromDTO(OrderDTO orderDTO, Orders order) {
        if (orderDTO == null || order == null) {
            return;
        }

        if (orderDTO.getId() != null) {
            order.setId(orderDTO.getId());
        }
        if (orderDTO.getOrderStatus() != null) {
            order.setOrderStatus(orderDTO.getOrderStatus());
        }
        if (orderDTO.getOrderDate() != null) {
            order.setOrderDate(orderDTO.getOrderDate());
        }
        if (orderDTO.getTotalAmount() != null) {
            order.setTotalAmount(orderDTO.getTotalAmount());
        }
        if (orderDTO.getDiscountRate() != null) {
            order.setDiscountRate(orderDTO.getDiscountRate());
        }
        if (orderDTO.getUserAddress() != null) {
            order.setUserAddress(orderDTO.getUserAddress());
        }
        if (orderDTO.getOrderOption() != null) {
            order.setOrderOption(orderDTO.getOrderOption());
        }

    }

    @Override
    public void updateOrderLineFromDTO(OrderLineDTO orderLineDTO, OrderLine orderLine) {
        if (orderLineDTO == null || orderLine == null) {
            return;
        }

        if (orderLineDTO.getId() != null) {
            orderLine.setId(orderLineDTO.getId());
        }

        orderLine.setQuantity(orderLineDTO.getQuantity());

        if (orderLineDTO.getPrice() != null) {
            orderLine.setPrice(orderLineDTO.getPrice());
        }
        if (orderLineDTO.getDiscountRate() != null) {
            orderLine.setDiscountRate(orderLineDTO.getDiscountRate());
        }
        if (orderLineDTO.getOrderId() != null) {
            orderLine.setOrderId(orderLineDTO.getOrderId());
        }
    }
}
