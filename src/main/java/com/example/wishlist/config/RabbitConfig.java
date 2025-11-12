package com.example.wishlist.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public Queue wishlistEventsQueue() {
        return new Queue("wishlist-events", true);
    }
}
