package io.elice.shoppingmall;

import io.elice.shoppingmall.order.mapper.OrderMapper;
import io.elice.shoppingmall.product.mapper.BookMapper;
import io.elice.shoppingmall.product.mapper.BookMapperImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public BookMapper bookMapper() {
        return new BookMapperImpl();
    }
}
