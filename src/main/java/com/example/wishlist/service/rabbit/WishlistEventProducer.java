package com.example.wishlist.service.rabbit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class WishlistEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public WishlistEventProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendEvent(String action, Long itemId, String title) {
        String message = String.format("%s|%d|%s", action, itemId, title);
        rabbitTemplate.convertAndSend("wishlist-events", message);
        System.out.println("Sent: " + message);
    }
}
