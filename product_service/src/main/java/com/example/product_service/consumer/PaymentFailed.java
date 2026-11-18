package com.example.product_service.consumer;

import com.example.product_service.consumer.dto.OrderCancelEvent;
import com.example.product_service.consumer.dto.OrderItem;
import com.example.product_service.consumer.dto.PaymentFailedEvent;
import com.example.product_service.entity.OutboxEvent;
import com.example.product_service.events.StockReleasedEvent;
import com.example.product_service.repository.OutboxEventRepository;
import com.example.product_service.repository.ProductRepository;
import com.example.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentFailed {
    private final ProductRepository repo;
    private final OutboxEventRepository outboxRepo;
    private final ObjectMapper mapper;
    private final ProductService service;

    @KafkaListener(topics = "payment_failed", groupId = "product-service")
    public void handlReleaseStock(String event)
    {
        log.warn("RECEIVED PAYMENT FAILED EVENT: {}", event);
        PaymentFailedEvent mess = mapper.readValue(event, PaymentFailedEvent.class);

        List<OrderItem> items = mess.getItems();

        service.releasedStock(items);
        StockReleasedEvent sre = new StockReleasedEvent();
        sre.setOrderId(mess.getOrderId());

        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setAggregateId(mess.getOrderId());
        outboxEvent.setPayload(mapper.writeValueAsString(sre));
        outboxEvent.setCreatedAt(LocalDateTime.now());
        outboxEvent.setAggregateType("released_stock");
        outboxEvent.setEventType("stock_released_event");
        outboxRepo.save(outboxEvent);
    }
}

