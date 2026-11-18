package com.example.order_service.consumer;

import com.example.order_service.consumer.dto.PaymentSucessEvent;
import com.example.order_service.consumer.dto.StockReleasedEvent;
import com.example.order_service.dto.req.OrderItemReq;
import com.example.order_service.entity.OrderItemEntity;
import com.example.order_service.enums.PaymentStatus;
import com.example.order_service.enums.StatusOrder;
import com.example.order_service.events.OrderCancelEvent;
import com.example.order_service.mapper.OrderItemMapper;
import com.example.order_service.producer.OrderEventProducer;
import com.example.order_service.repository.OrderItemRepository;
import com.example.order_service.repository.OrderRepository;
import com.example.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderConsumerLock {
    private final OrderService service;
    private final ObjectMapper mapper;
    private final KafkaTemplate<String, Object> template;
    private final OrderEventProducer producer;
    private final OrderItemRepository itemRepo;
    private final OrderRepository repo;
    private final OrderItemMapper itemMapper;

//    @KafkaListener(topics = "product_locked", groupId = "order-service")
//    public void handleProductLock(String orderDto) {
//        OrderDto order = mapper.readValue(orderDto, OrderDto.class);
//        log.info("Received OrderDto: {}", order);
//        service.updatePaymentStatus(order.getOrderId(), PaymentStatus.SUCCESS);
//        service.updateOrderStatus(order.getOrderId(), StatusOrder.CREATED);
//    }
    @KafkaListener(topics = "payment_sucess", groupId = "order-service")
    public void handleProductLock(String orderDto) {
        PaymentSucessEvent order = mapper.readValue(orderDto, PaymentSucessEvent.class);
        log.info("Received PaymentSucess: {}", order);
        service.updatePaymentStatus(order.getOrderId(), PaymentStatus.SUCCESS);
        service.updateOrderStatus(order.getOrderId(), StatusOrder.CREATED);
    }

    @KafkaListener(topics = "stock_released", groupId = "order-service")
    public void handleProductOutOfStock(String event) {
        StockReleasedEvent mess = mapper.readValue(event, StockReleasedEvent.class);
        log.info("Received stock released stock event with order id: {}", mess.getOrderId());
        service.updatePaymentStatus(mess.getOrderId(), PaymentStatus.FAILED);
        service.updateOrderStatus(mess.getOrderId(), StatusOrder.CANCELED);
    }

}
