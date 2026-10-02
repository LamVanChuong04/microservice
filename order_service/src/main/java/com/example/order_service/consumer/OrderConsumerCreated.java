package com.example.order_service.consumer;

import com.example.order_service.consumer.dto.Order;
import com.example.order_service.consumer.dto.test;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Test;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderConsumerCreated {
    @KafkaListener(topics = "test", groupId = "product_service")
    public void handleOrderCreatedEvent(test test) {
        log.info("Received Order Created Event: {}", test);
    }
}
