package io.elice.shoppingmall.order.service;

import io.elice.shoppingmall.order.entity.OrderLine;
import io.elice.shoppingmall.order.repository.OrderLineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class OrderLineService {

    @Autowired
    private OrderLineRepository orderLineRepository;

    public List<OrderLine> getOrderLinesByOrderId(Long orderId) {
        return orderLineRepository.findByOrdersId(orderId);
    }

    public OrderLine getOrderLineById(Long id) {
        return orderLineRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("OrderLine not found for id: " + id));
    }

    @Transactional
    public OrderLine createOrderLine(OrderLine orderLine) {
        return orderLineRepository.save(orderLine);
    }

    @Transactional
    public void deleteOrderLine(Long id) {
        orderLineRepository.deleteById(id);
    }
}
