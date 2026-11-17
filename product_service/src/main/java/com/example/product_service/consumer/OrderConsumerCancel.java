package com.example.product_service.consumer;

import com.example.product_service.consumer.dto.OrderCancelEvent;
import com.example.product_service.consumer.dto.OrderItem;
import com.example.product_service.dto.req.LockProductItem;
import com.example.product_service.dto.req.LockProductReq;
import com.example.product_service.entity.ProductEntity;
import com.example.product_service.exception.BusinessException;
import com.example.product_service.repository.ProductRepository;
import com.example.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderConsumerCancel {
    private final ProductRepository repo;
    private final ObjectMapper mapper;
    private final ProductService service;

    @KafkaListener(topics = "order_cancel", groupId = "product_service")
    public void handlReleaseStock(String event)
    {
        log.warn("ORDER_CANCEL RECEIVED: {}", event);
        OrderCancelEvent mess = mapper.readValue(event, OrderCancelEvent.class);

        List<OrderItem> items = mess.getOrderItems();

        service.releasedStock(items);
    }
}

