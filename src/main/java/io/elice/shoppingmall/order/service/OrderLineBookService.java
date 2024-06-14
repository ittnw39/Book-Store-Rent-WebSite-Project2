package io.elice.shoppingmall.order.service;

import io.elice.shoppingmall.order.DTO.OrderLineBookDTO;
import io.elice.shoppingmall.order.entity.OrderLineBook;
import io.elice.shoppingmall.order.mapper.OrderMapper;
import io.elice.shoppingmall.order.repository.OrderLineBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderLineBookService {

    private final OrderLineBookRepository orderLineBookRepository;
    private final OrderMapper orderMapper;

    public OrderLineBookDTO getOrderLineBookById(Long orderLineId){
        OrderLineBook orderLineBook = orderLineBookRepository.findByOrderLineId(orderLineId);
        return orderMapper.toOrderLineBookDTO(orderLineBook);
    }

    @Transactional
    public OrderLineBook createOrderLineBook(OrderLineBookDTO orderLineBookDTO) {
        OrderLineBook orderLineBook = OrderMapper.INSTANCE.toOrderLineBookEntity(orderLineBookDTO);
        return orderLineBookRepository.save(orderLineBook);
    }
}
