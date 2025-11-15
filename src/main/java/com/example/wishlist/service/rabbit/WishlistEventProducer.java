package com.example.wishlist.service.rabbit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WishlistEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public WishlistEventProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendEvent(String action, Long itemId, String title) {
        String message = String.format("%s|%d|%s", action, itemId, title);
        rabbitTemplate.convertAndSend("wishlist-events", message);
        log.info("Sent event to RabbitMQ: {}", message);
    }
}
