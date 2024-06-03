package io.elice.shoppingmall;

import io.elice.shoppingmall.order.mapper.OrderMapper;
import io.elice.shoppingmall.order.mapper.OrderMapperImpl;
import io.elice.shoppingmall.product.mapper.BookMapper;
import io.elice.shoppingmall.product.mapper.BookMapperImpl;
import io.elice.shoppingmall.product.mapper.ReviewMapper;
import io.elice.shoppingmall.product.mapper.ReviewMapperImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public BookMapper bookMapper() {
        return new BookMapperImpl();
    }

    @Bean
    public ReviewMapper reviewMapper() { return new ReviewMapperImpl(); }

    @Bean
    public OrderMapper orderMapper() {
        return new OrderMapperImpl();
    }
}
