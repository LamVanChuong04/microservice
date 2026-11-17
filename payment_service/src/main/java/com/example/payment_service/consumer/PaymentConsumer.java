package com.example.payment_service.consumer;

import com.example.payment_service.consumer.dto.ProductLockedEvent;

import com.example.payment_service.entity.OutboxEvent;
import com.example.payment_service.events.PaymentSucess;
import com.example.payment_service.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PaymentConsumer {
    private final KafkaTemplate<String, Object> template;
    private final ObjectMapper object;
    private final OutboxEventRepository repo;

    @KafkaListener(topics = "product_locked.events", groupId = "payment-service")
    public void processPayment(String event){
        double random = Math.random();
        ProductLockedEvent mess = object.readValue(event, ProductLockedEvent.class);
        if(random > 0.5)
        {
            OutboxEvent outbox = new OutboxEvent();
            outbox.setPayload(object.writeValueAsString(mess));
            outbox.setEventType("payment_sucess");
            outbox.setAggregateId(mess.getOrderId());
            outbox.setAggregateType("payment_sucess");
            outbox.setCreatedAt(LocalDateTime.now());
            repo.save(outbox);
        }
        else {
            OutboxEvent outbox = new OutboxEvent();
            outbox.setPayload(object.writeValueAsString(mess));
            outbox.setEventType("payment_failed");
            outbox.setAggregateId(mess.getOrderId());
            outbox.setAggregateType("payment_failed");
            outbox.setCreatedAt(LocalDateTime.now());
            repo.save(outbox);
        }

    }
}
