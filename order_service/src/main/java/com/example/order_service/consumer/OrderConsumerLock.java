package com.example.order_service.consumer;

import com.example.order_service.consumer.dto.PaymentSucess;
import com.example.order_service.dto.req.OrderItemReq;
import com.example.order_service.entity.OrderEntity;
import com.example.order_service.entity.OrderItemEntity;
import com.example.order_service.enums.PaymentStatus;
import com.example.order_service.enums.StatusOrder;
import com.example.order_service.events.OrderCancelEvent;
import com.example.order_service.exception.BusinessException;
import com.example.order_service.mapper.OrderItemMapper;
import com.example.order_service.mapper.OrderMapper;
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
    @KafkaListener(topics = "payment_sucess.events", groupId = "order-service")
    public void handleProductLock(String orderDto) {
        PaymentSucess order = mapper.readValue(orderDto, PaymentSucess.class);
        log.info("Received PaymentSucess: {}", order);
        service.updatePaymentStatus(order.getOrderId(), PaymentStatus.SUCCESS);
        service.updateOrderStatus(order.getOrderId(), StatusOrder.CREATED);
    }

    @KafkaListener(topics = "payment_failed.events", groupId = "order-service")
    public void handleProductOutOfStock(String orderDto) {
        PaymentSucess order = mapper.readValue(orderDto, PaymentSucess.class);
        log.info("Received OrderDto With Product Out Of Stock: {}", order);
        service.updatePaymentStatus(order.getOrderId(), PaymentStatus.FAILED);
        service.updateOrderStatus(order.getOrderId(), StatusOrder.CANCELED);

        // tim list items theo order id
        List<OrderItemEntity> items = itemRepo.findByOrderId(order.getOrderId());
        List<OrderItemReq> res = items.stream().map(item -> itemMapper.fromEntity(item)).collect(Collectors.toList());
        OrderCancelEvent event = new OrderCancelEvent();
        event.setOrderItems(res);
        producer.handlePaymentFailed(event);
    }

}
