package com.example.product_service.consumer;

import com.example.product_service.consumer.dto.OrderCreatedEvent;
import com.example.product_service.dto.req.LockProductItem;
import com.example.product_service.dto.req.LockProductReq;
import com.example.product_service.events.OrderDto;
import com.example.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderConsumerCreated {
    private final ProductService productService;
    private final KafkaTemplate<String, Object> kafkaTemplate;


    @KafkaListener(topics = "created_order", groupId = "product_service")
    @RetryableTopic(attempts = "4",
            backOff = @BackOff(delay = 2000, multiplier = 2),
            include = {NullPointerException.class, IllegalArgumentException.class, RuntimeException.class})
    public void handleOrderCreatedEvent(OrderCreatedEvent orderCreatedEvent) {
        log.info("Received Order Created Event: {}", orderCreatedEvent);
//        if(orderCreatedEvent != null) {
//            throw new RuntimeException("failed");
//        }
        List<LockProductItem> lockProductItems = new ArrayList<>();

        orderCreatedEvent.getOrderItems().forEach(orderItem -> {
            LockProductItem lockProductItem = new LockProductItem();
            lockProductItem.setProductId(orderItem.getProductId());
            lockProductItem.setQuantity(orderItem.getQuantity());
            lockProductItems.add(lockProductItem);
        });

        LockProductReq lockProductReq = new LockProductReq();
        lockProductReq.setItems(lockProductItems);

        // lock product
        productService.distributeLock(lockProductReq);
        log.info("success to lock product item of {}", orderCreatedEvent.getId());

        // NEW --> PREPARED / LOCKED
        // publish message to: product_locked  (message: order_id)
        // order_service update order status
        kafkaTemplate.send("product_locked", new OrderDto(orderCreatedEvent.getId()));
        log.info("sent message with order-id: {}", orderCreatedEvent.getId());
    }
}
