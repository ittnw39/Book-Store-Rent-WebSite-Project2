package io.elice.shoppingmall.order.mapper;

import io.elice.shoppingmall.order.DTO.OrderDTO;
import io.elice.shoppingmall.order.DTO.OrderLineBookDTO;
import io.elice.shoppingmall.order.DTO.OrderLineDTO;
import io.elice.shoppingmall.order.entity.OrderLine;
import io.elice.shoppingmall.order.entity.OrderLineBook;
import io.elice.shoppingmall.order.entity.Orders;
import io.elice.shoppingmall.product.entity.Book;
import io.elice.shoppingmall.user.entity.User;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-07-01T16:15:15+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.10 (Oracle Corporation)"
)
public class OrderMapperImpl implements OrderMapper {

    @Override
    public Orders toOrderEntity(OrderDTO orderDTO, User user) {
        if ( orderDTO == null && user == null ) {
            return null;
        }

        Orders orders = new Orders();

        if ( orderDTO != null ) {
            orders.setUser( orderDTOToUser( orderDTO ) );
            orders.setId( orderDTO.getId() );
            orders.setOrderLine( orderLineDTOListToOrderLineList( orderDTO.getOrderLines() ) );
            orders.setOrderDate( orderDTO.getOrderDate() );
            orders.setOrderStatus( orderDTO.getOrderStatus() );
            orders.setTotalAmount( orderDTO.getTotalAmount() );
            orders.setDiscountRate( orderDTO.getDiscountRate() );
            orders.setUserAddress( orderDTO.getUserAddress() );
            orders.setOrderOption( orderDTO.getOrderOption() );
            orders.setRequest( orderDTO.getRequest() );
        }

        return orders;
    }

    @Override
    public OrderDTO toOrderDTO(Orders order) {
        if ( order == null ) {
            return null;
        }

        OrderDTO orderDTO = new OrderDTO();

        orderDTO.setOrderLines( orderLineListToOrderLineDTOList( order.getOrderLine() ) );
        orderDTO.setUserId( orderUserId( order ) );
        orderDTO.setId( order.getId() );
        orderDTO.setOrderDate( order.getOrderDate() );
        orderDTO.setOrderStatus( order.getOrderStatus() );
        orderDTO.setTotalAmount( order.getTotalAmount() );
        orderDTO.setDiscountRate( order.getDiscountRate() );
        orderDTO.setUserAddress( order.getUserAddress() );
        orderDTO.setOrderOption( order.getOrderOption() );
        orderDTO.setRequest( order.getRequest() );

        return orderDTO;
    }

    @Override
    public OrderLine toOrderLineEntity(OrderLineDTO orderLineDTO) {
        if ( orderLineDTO == null ) {
            return null;
        }

        OrderLine orderLine = new OrderLine();

        orderLine.setOrders( orderLineDTOToOrders( orderLineDTO ) );
        orderLine.setId( orderLineDTO.getId() );
        orderLine.setQuantity( orderLineDTO.getQuantity() );
        orderLine.setPrice( orderLineDTO.getPrice() );
        orderLine.setDiscountRate( orderLineDTO.getDiscountRate() );
        orderLine.setOrderLineBooks( orderLineBookDTOListToOrderLineBookList( orderLineDTO.getOrderLineBooks() ) );

        return orderLine;
    }

    @Override
    public OrderLineDTO toOrderLineDTO(OrderLine orderLine) {
        if ( orderLine == null ) {
            return null;
        }

        OrderLineDTO orderLineDTO = new OrderLineDTO();

        orderLineDTO.setOrderId( orderLineOrdersId( orderLine ) );
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

        if ( order.getUser() == null ) {
            order.setUser( new User() );
        }
        orderDTOToUser1( orderDTO, order.getUser() );
        if ( order.getOrderLine() != null ) {
            List<OrderLine> list = orderLineDTOListToOrderLineList( orderDTO.getOrderLines() );
            if ( list != null ) {
                order.getOrderLine().clear();
                order.getOrderLine().addAll( list );
            }
            else {
                order.setOrderLine( null );
            }
        }
        else {
            List<OrderLine> list = orderLineDTOListToOrderLineList( orderDTO.getOrderLines() );
            if ( list != null ) {
                order.setOrderLine( list );
            }
        }
        order.setId( orderDTO.getId() );
        order.setOrderDate( orderDTO.getOrderDate() );
        order.setOrderStatus( orderDTO.getOrderStatus() );
        order.setTotalAmount( orderDTO.getTotalAmount() );
        order.setDiscountRate( orderDTO.getDiscountRate() );
        order.setUserAddress( orderDTO.getUserAddress() );
        order.setOrderOption( orderDTO.getOrderOption() );
        order.setRequest( orderDTO.getRequest() );
    }

    @Override
    public void updateOrderLineFromDTO(OrderLineDTO orderLineDTO, OrderLine orderLine) {
        if ( orderLineDTO == null ) {
            return;
        }

        if ( orderLine.getOrders() == null ) {
            orderLine.setOrders( new Orders() );
        }
        orderLineDTOToOrders1( orderLineDTO, orderLine.getOrders() );
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

    protected User orderDTOToUser(OrderDTO orderDTO) {
        if ( orderDTO == null ) {
            return null;
        }

        User user = new User();

        user.setId( orderDTO.getUserId() );

        return user;
    }

    protected List<OrderLine> orderLineDTOListToOrderLineList(List<OrderLineDTO> list) {
        if ( list == null ) {
            return null;
        }

        List<OrderLine> list1 = new ArrayList<OrderLine>( list.size() );
        for ( OrderLineDTO orderLineDTO : list ) {
            list1.add( toOrderLineEntity( orderLineDTO ) );
        }

        return list1;
    }

    protected List<OrderLineDTO> orderLineListToOrderLineDTOList(List<OrderLine> list) {
        if ( list == null ) {
            return null;
        }

        List<OrderLineDTO> list1 = new ArrayList<OrderLineDTO>( list.size() );
        for ( OrderLine orderLine : list ) {
            list1.add( toOrderLineDTO( orderLine ) );
        }

        return list1;
    }

    private Long orderUserId(Orders orders) {
        if ( orders == null ) {
            return null;
        }
        User user = orders.getUser();
        if ( user == null ) {
            return null;
        }
        Long id = user.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    protected Orders orderLineDTOToOrders(OrderLineDTO orderLineDTO) {
        if ( orderLineDTO == null ) {
            return null;
        }

        Orders orders = new Orders();

        orders.setId( orderLineDTO.getOrderId() );

        return orders;
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

    private Long orderLineOrdersId(OrderLine orderLine) {
        if ( orderLine == null ) {
            return null;
        }
        Orders orders = orderLine.getOrders();
        if ( orders == null ) {
            return null;
        }
        Long id = orders.getId();
        if ( id == null ) {
            return null;
        }
        return id;
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

        OrderLine orderLine = new OrderLine();

        orderLine.setId( orderLineBookDTO.getOrderLineId() );

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

    protected void orderDTOToUser1(OrderDTO orderDTO, User mappingTarget) {
        if ( orderDTO == null ) {
            return;
        }

        mappingTarget.setId( orderDTO.getUserId() );
    }

    protected void orderLineDTOToOrders1(OrderLineDTO orderLineDTO, Orders mappingTarget) {
        if ( orderLineDTO == null ) {
            return;
        }

        mappingTarget.setId( orderLineDTO.getOrderId() );
    }
}
