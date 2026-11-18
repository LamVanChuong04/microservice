package com.example.payment_service.consumer;

import com.example.payment_service.consumer.dto.StockReservedEvent;
import com.example.payment_service.entity.OutboxEvent;
import com.example.payment_service.events.PaymentCompletedEvent;
import com.example.payment_service.events.PaymentFailedEvent;
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

    @KafkaListener(topics = "product_locked", groupId = "payment-service")
    public void processPayment(String event){
        double random = Math.random();
        StockReservedEvent mess = object.readValue(event, StockReservedEvent.class);
        if(random > 0.5)
        {
            PaymentCompletedEvent pce = new PaymentCompletedEvent();
            pce.setOrderId(mess.getOrderId());
            OutboxEvent outbox = new OutboxEvent();
            outbox.setPayload(object.writeValueAsString(pce));
            outbox.setEventType("payment_sucess");
            outbox.setAggregateId(mess.getOrderId());
            outbox.setAggregateType("payment_sucess");
            outbox.setCreatedAt(LocalDateTime.now());
            repo.save(outbox);
        }
        else {
            PaymentFailedEvent pfe = new PaymentFailedEvent();
            pfe.setItems(mess.getItems());
            pfe.setOrderId(mess.getOrderId());
            OutboxEvent outbox = new OutboxEvent();
            outbox.setPayload(object.writeValueAsString(pfe));
            outbox.setEventType("payment_failed");
            outbox.setAggregateId(mess.getOrderId());
            outbox.setAggregateType("payment_failed");
            outbox.setCreatedAt(LocalDateTime.now());
            repo.save(outbox);
        }

    }
}
