package com.example.product_service.consumer;

import com.example.product_service.consumer.dto.OrderCreatedEvent;
import com.example.product_service.dto.req.LockProductItem;
import com.example.product_service.dto.req.LockProductReq;
import com.example.product_service.entity.OutboxEvent;
import com.example.product_service.events.StockReservedEvent;
import com.example.product_service.repository.OutboxEventRepository;
import com.example.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderConsumerCreated {
    private final ProductService productService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper mapper;
    private final OutboxEventRepository outboxRepo;


    @KafkaListener(topics = "order_created", groupId = "product-service")
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
        //productService.lock(lockProductReq);

        // 2. permistic lock: select ... for update
        //productService.lockForUpdate(lockProductReq);

        try{
            // 3. distributed lock (redis)
            productService.distributedLock(lockProductReq);

            log.info("success to lock product item of {}", event.getId());

            // NEW --> PREPARED / LOCKED
            // publish message to: product_locked  (message: order_id)
            // order_service update order status
            StockReservedEvent mess = new StockReservedEvent();
            mess.setOrderId(event.getId());
            mess.setUserId(event.getCustomerId());
            mess.setMethod(event.getPaymentMethod());
            mess.setAmount(event.getAmount());
            mess.setItems(event.getOrderItems());

            OutboxEvent outbox = new OutboxEvent();
            outbox.setCreatedAt(LocalDateTime.now());
            outbox.setPayload(mapper.writeValueAsString(mess));
            outbox.setAggregateId(event.getId());
            outbox.setEventType("stock_reserved_event");
            outbox.setAggregateType("product_locked");
            outboxRepo.save(outbox);
            log.info("Product locked Event: {}", outbox);


//            kafkaTemplate.send("product_locked", mess);
//            log.info("sent message with order-id: {}", event.getId());
        }catch(Exception e){
            System.out.println(e);
        }
    }

}
