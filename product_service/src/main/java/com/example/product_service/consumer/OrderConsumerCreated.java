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
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderConsumerCreated {
    private final ProductService productService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper mapper;


    @KafkaListener(topics = "order.events", groupId = "product_service")
//    @RetryableTopic(attempts = "4",
//            backOff = @BackOff(delay = 2000, multiplier = 2),
//            exclude = {NullPointerException.class, IllegalArgumentException.class})
    public void handleOrderCreatedEvent(String orderCreatedEvent) {
        OrderCreatedEvent event = mapper.readValue(orderCreatedEvent, OrderCreatedEvent.class);
        log.info("Received Order Created Event: {}", orderCreatedEvent);
//        if(orderCreatedEvent != null) {
//            throw new RuntimeException("failed");
//        }
        List<LockProductItem> lockProductItems = new ArrayList<>();

        event.getOrderItems().forEach(orderItem -> {
            LockProductItem lockProductItem = new LockProductItem();
            lockProductItem.setProductId(orderItem.getProductId());
            lockProductItem.setQuantity(orderItem.getQuantity());
            lockProductItems.add(lockProductItem);
        });

        LockProductReq lockProductReq = new LockProductReq();
        lockProductReq.setItems(lockProductItems);

        // lock product
        // 1. No lock
        // productService.lock(lockProductReq);

        // 2. pessimistic lock: select ... for update
        // productService.lockForUpdate(lockProductReq);

        try{
            // 3. distributed lock (redis)
            productService.distributedLock(lockProductReq);

            log.info("success to lock product item of {}", event.getId());

            // NEW --> PREPARED / LOCKED
            // publish message to: product_locked  (message: order_id)
            // order_service update order status
            kafkaTemplate.send("product_locked", new OrderDto(event.getId()));
            log.info("sent message with order-id: {}", event.getId());
        }catch(Exception e){
            kafkaTemplate.send("product_outof_stock", new OrderDto(event.getId()));
            log.info("sent message with order-id: {}", event.getId());
        }
    }

}
