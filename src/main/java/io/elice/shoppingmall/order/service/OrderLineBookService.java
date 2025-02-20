package io.elice.shoppingmall.order.service;

import io.elice.shoppingmall.order.DTO.OrderLineBookDTO;
import io.elice.shoppingmall.order.entity.OrderLine;
import io.elice.shoppingmall.order.entity.OrderLineBook;
import io.elice.shoppingmall.order.mapper.OrderMapper;
import io.elice.shoppingmall.order.repository.OrderLineBookRepository;
import io.elice.shoppingmall.order.repository.OrderLineRepository;
import io.elice.shoppingmall.product.entity.Book;
import io.elice.shoppingmall.product.repository.BookRepository;
import io.elice.shoppingmall.product.service.BookService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderLineBookService {

    private final OrderLineBookRepository orderLineBookRepository;
    private final OrderLineRepository orderLineRepository;
    private final OrderMapper orderMapper;
    private final BookRepository bookRepository;
    private final BookService bookService;

    public OrderLineBookDTO getOrderLineBookById(Long orderLineId){
        OrderLineBook orderLineBook = orderLineBookRepository.findByOrderLineId(orderLineId);
        return orderMapper.toOrderLineBookDTO(orderLineBook);
    }

    public OrderLineBook createOrderLineBook(OrderLineBookDTO orderLineBookDTO) {
        // Fetch the book and order line entities
        Book book = bookRepository.findById(orderLineBookDTO.getBookId())
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));
        OrderLine orderLine = orderLineRepository.findById(orderLineBookDTO.getOrderLineId())
                .orElseThrow(() -> new EntityNotFoundException("Order Line not found"));

        // Create the order line book entity
        OrderLineBook orderLineBook = new OrderLineBook();
        orderLineBook.setBook(book);
        orderLineBook.setOrderLine(orderLine);
        orderLineBook.setQuantity(orderLineBookDTO.getQuantity());

        bookService.reduceStock(orderLineBook.getBook().getId(), orderLineBook.getQuantity());
        bookService.addOrderCount(orderLineBook.getBook().getId());

        // Save the order line book entity
        return orderLineBookRepository.save(orderLineBook);
    }
}
