package com.example.order_service.consumer;

import com.example.order_service.consumer.dto.OrderDto;
import com.example.order_service.enums.StatusOrder;
import com.example.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderConsumerLock {
    private final OrderService service;
    private final ObjectMapper mapper;

    @KafkaListener(topics = "product_locked", groupId = "order-service")
    public void handleProductLock(String orderDto) {
        OrderDto order = mapper.readValue(orderDto, OrderDto.class);
        log.info("Received OrderDto: {}", order);
        service.updateOrderStatus(order.getOrderId(), StatusOrder.CREATED);
    }

    @KafkaListener(topics = "product_outof_stock", groupId = "order-service")
    public void handleProductOutOfStock(String orderDto) {
        OrderDto order = mapper.readValue(orderDto, OrderDto.class);
        log.info("Received OrderDto With Product Out Of Stock: {}", order);
        service.updateOrderStatus(order.getOrderId(), StatusOrder.CANCELED);
    }

}
