package com.example.product_service.consumer;

import com.example.product_service.consumer.dto.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderConsumerCreated {
    @KafkaListener(topics = "created_order", groupId = "product_service")
    public void handleOrderCreatedEvent(Order order) {
        log.info("Received Order Created Event: {}", order);
    }
}
