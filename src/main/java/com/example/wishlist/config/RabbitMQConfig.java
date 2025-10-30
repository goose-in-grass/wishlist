//package com.example.wishlist.config;
//
//import org.springframework.amqp.core.Queue;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//
//@Configuration
//public class RabbitMQConfig {
//
//    @Value("${rabbitmq.queue.name}")
//    private String queueName;
//
//    @Value("${rabbitmq.queue_with_delay.name}")
//    private String queueWithDelayName;
//
//    /**
//     * Конфигурируем очередь
//     * @return Очередь RabbitMQ
//     */
//    @Bean
//    public Queue queue() {
//        return new Queue(queueName, false);
//    }
//
//    @Bean
//    public Queue queueWithDelay() {
//        return new Queue(queueWithDelayName, false);
//    }
//
//}
