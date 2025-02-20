package io.elice.shoppingmall.order.mapper;

import io.elice.shoppingmall.order.DTO.OrderDTO;
import io.elice.shoppingmall.order.DTO.OrderLineBookDTO;
import io.elice.shoppingmall.order.DTO.OrderLineDTO;
import io.elice.shoppingmall.order.entity.OrderLine;
import io.elice.shoppingmall.order.entity.OrderLineBook;
import io.elice.shoppingmall.order.entity.OrderOption;
import io.elice.shoppingmall.order.entity.Orders;
import io.elice.shoppingmall.product.entity.Book;
import io.elice.shoppingmall.user.entity.User;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
        value = "org.mapstruct.ap.MappingProcessor",
        date = "2024-06-01T00:25:27+0900",
        comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.10 (Azul Systems, Inc.)"
)
public class OrderMapperImpl implements OrderMapper {

    @Override
    public Orders toOrderEntity(OrderDTO orderDTO, User user) {
        if ( orderDTO == null ) {
            return null;
        }

//        Long id = null;
//        Date orderDate = null;
//        String orderStatus = null;
//        BigDecimal totalAmount = null;
//        BigDecimal discountRate = null;
//        String userAddress = null;
//        OrderOption orderOption = null;
//
//        id = orderDTO.getId();
//        orderDate = orderDTO.getOrderDate();
//        orderStatus = orderDTO.getOrderStatus();
//        totalAmount = orderDTO.getTotalAmount();
//        discountRate = orderDTO.getDiscountRate();
//        userAddress = orderDTO.getUserAddress();
//        orderOption = orderDTO.getOrderOption();

        Orders orders = new Orders();
        orders.setId(orderDTO.getId());
        orders.setOrderDate(orderDTO.getOrderDate());
        orders.setOrderStatus(orderDTO.getOrderStatus());
        orders.setTotalAmount(orderDTO.getTotalAmount());
        orders.setDiscountRate(orderDTO.getDiscountRate());
        orders.setUserAddress(orderDTO.getUserAddress());
        orders.setOrderOption(orderDTO.getOrderOption());
        orders.setUser(user); // User 객체 설정

        return orders;
    }

    @Override
    public OrderDTO toOrderDTO(Orders order) {
        if ( order == null ) {
            return null;
        }

        OrderDTO orderDTO = new OrderDTO();

        orderDTO.setId( order.getId() );
        orderDTO.setOrderDate( order.getOrderDate() );
        orderDTO.setOrderStatus( order.getOrderStatus() );
        orderDTO.setTotalAmount( order.getTotalAmount() );
        orderDTO.setDiscountRate( order.getDiscountRate() );
        orderDTO.setUserAddress( order.getUserAddress() );
        orderDTO.setOrderOption( order.getOrderOption() );
        orderDTO.setUserId(order.getUser().getId());

        return orderDTO;
    }

    @Override
    public OrderLine toOrderLineEntity(OrderLineDTO orderLineDTO) {
        if ( orderLineDTO == null ) {
            return null;
        }

        Long id = null;
        int quantity = 0;
        BigDecimal price = null;
        BigDecimal discountRate = null;
        List<OrderLineBook> orderLineBooks = null;

        id = orderLineDTO.getId();
        quantity = orderLineDTO.getQuantity();
        price = orderLineDTO.getPrice();
        discountRate = orderLineDTO.getDiscountRate();
        orderLineBooks = orderLineBookDTOListToOrderLineBookList( orderLineDTO.getOrderLineBooks() );

        Orders orders = null;

        OrderLine orderLine = new OrderLine( id, quantity, price, discountRate, orders, orderLineBooks );

        return orderLine;
    }

    @Override
    public OrderLineDTO toOrderLineDTO(OrderLine orderLine) {
        if ( orderLine == null ) {
            return null;
        }

        OrderLineDTO orderLineDTO = new OrderLineDTO();

        orderLineDTO.setId( orderLine.getId() );
        orderLineDTO.setQuantity( orderLine.getQuantity() );
        orderLineDTO.setPrice( orderLine.getPrice() );
        orderLineDTO.setDiscountRate( orderLine.getDiscountRate() );
        orderLineDTO.setOrderLineBooks( orderLineBookListToOrderLineBookDTOList( orderLine.getOrderLineBooks() ) );

        return orderLineDTO;
    }

    @Override
    public OrderLineBook toOrderLineBookEntity(OrderLineBookDTO orderLineBookDTO) {
        if ( orderLineBookDTO == null ) {
            return null;
        }

        OrderLineBook orderLineBook = new OrderLineBook();

        orderLineBook.setBook( orderLineBookDTOToBook( orderLineBookDTO ) );
        orderLineBook.setOrderLine( orderLineBookDTOToOrderLine( orderLineBookDTO ) );
        orderLineBook.setQuantity( orderLineBookDTO.getQuantity() );

        return orderLineBook;
    }

    @Override
    public OrderLineBookDTO toOrderLineBookDTO(OrderLineBook orderLineBook) {
        if ( orderLineBook == null ) {
            return null;
        }

        OrderLineBookDTO orderLineBookDTO = new OrderLineBookDTO();

        orderLineBookDTO.setBookId( orderLineBookBookId( orderLineBook ) );
        orderLineBookDTO.setOrderLineId( orderLineBookOrderLineId( orderLineBook ) );
        orderLineBookDTO.setQuantity( orderLineBook.getQuantity() );

        return orderLineBookDTO;
    }

    @Override
    public void updateOrderFromDTO(OrderDTO orderDTO, Orders order) {
        if ( orderDTO == null ) {
            return;
        }

        order.setId( orderDTO.getId() );
        order.setOrderDate( orderDTO.getOrderDate() );
        order.setOrderStatus( orderDTO.getOrderStatus() );
        order.setTotalAmount( orderDTO.getTotalAmount() );
        order.setDiscountRate( orderDTO.getDiscountRate() );
        order.setUserAddress( orderDTO.getUserAddress() );
        order.setOrderOption( orderDTO.getOrderOption() );
    }

    @Override
    public void updateOrderLineFromDTO(OrderLineDTO orderLineDTO, OrderLine orderLine) {
        if ( orderLineDTO == null ) {
            return;
        }

        orderLine.setId( orderLineDTO.getId() );
        orderLine.setQuantity( orderLineDTO.getQuantity() );
        orderLine.setPrice( orderLineDTO.getPrice() );
        orderLine.setDiscountRate( orderLineDTO.getDiscountRate() );
        if ( orderLine.getOrderLineBooks() != null ) {
            List<OrderLineBook> list = orderLineBookDTOListToOrderLineBookList( orderLineDTO.getOrderLineBooks() );
            if ( list != null ) {
                orderLine.getOrderLineBooks().clear();
                orderLine.getOrderLineBooks().addAll( list );
            }
            else {
                orderLine.setOrderLineBooks( null );
            }
        }
        else {
            List<OrderLineBook> list = orderLineBookDTOListToOrderLineBookList( orderLineDTO.getOrderLineBooks() );
            if ( list != null ) {
                orderLine.setOrderLineBooks( list );
            }
        }
    }

    protected List<OrderLineBook> orderLineBookDTOListToOrderLineBookList(List<OrderLineBookDTO> list) {
        if ( list == null ) {
            return null;
        }

        List<OrderLineBook> list1 = new ArrayList<OrderLineBook>( list.size() );
        for ( OrderLineBookDTO orderLineBookDTO : list ) {
            list1.add( toOrderLineBookEntity( orderLineBookDTO ) );
        }

        return list1;
    }

    protected List<OrderLineBookDTO> orderLineBookListToOrderLineBookDTOList(List<OrderLineBook> list) {
        if ( list == null ) {
            return null;
        }

        List<OrderLineBookDTO> list1 = new ArrayList<OrderLineBookDTO>( list.size() );
        for ( OrderLineBook orderLineBook : list ) {
            list1.add( toOrderLineBookDTO( orderLineBook ) );
        }

        return list1;
    }

    protected Book orderLineBookDTOToBook(OrderLineBookDTO orderLineBookDTO) {
        if ( orderLineBookDTO == null ) {
            return null;
        }

        Book book = new Book();

        book.setId( orderLineBookDTO.getBookId() );

        return book;
    }

    protected OrderLine orderLineBookDTOToOrderLine(OrderLineBookDTO orderLineBookDTO) {
        if ( orderLineBookDTO == null ) {
            return null;
        }

        Long id = null;

        id = orderLineBookDTO.getOrderLineId();

        int quantity = 0;
        BigDecimal price = null;
        BigDecimal discountRate = null;
        Orders orders = null;
        List<OrderLineBook> orderLineBooks = null;

        OrderLine orderLine = new OrderLine( id, quantity, price, discountRate, orders, orderLineBooks );

        return orderLine;
    }

    private Long orderLineBookBookId(OrderLineBook orderLineBook) {
        if ( orderLineBook == null ) {
            return null;
        }
        Book book = orderLineBook.getBook();
        if ( book == null ) {
            return null;
        }
        Long id = book.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long orderLineBookOrderLineId(OrderLineBook orderLineBook) {
        if ( orderLineBook == null ) {
            return null;
        }
        OrderLine orderLine = orderLineBook.getOrderLine();
        if ( orderLine == null ) {
            return null;
        }
        Long id = orderLine.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}