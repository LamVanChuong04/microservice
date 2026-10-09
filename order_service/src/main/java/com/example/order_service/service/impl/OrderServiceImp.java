package com.example.order_service.service.impl;

import com.example.order_service.client.ProductClient;
import com.example.order_service.dto.client.ProductDto;
import com.example.order_service.dto.req.CreateOrderReq;
import com.example.order_service.dto.req.OrderItemReq;
import com.example.order_service.dto.req.ProductFilter;
import com.example.order_service.dto.res.OrderRes;
import com.example.order_service.entity.OrderEntity;
import com.example.order_service.entity.OrderItemEntity;
import com.example.order_service.entity.OutboxEvent;
import com.example.order_service.enums.PaymentStatus;
import com.example.order_service.enums.StatusOrder;
import com.example.order_service.events.OrderCreatedEvent;
import com.example.order_service.exception.BusinessException;
import com.example.order_service.mapper.OrderMapper;
import com.example.order_service.repository.OrderItemRepository;
import com.example.order_service.repository.OrderRepository;
import com.example.order_service.repository.OutboxEventRepository;
import com.example.order_service.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImp implements OrderService {
    private final OrderMapper orderMapper;
    private final OrderRepository repo;
    private final OrderItemRepository itemRepo;
    private final ProductClient productClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final OutboxEventRepository outboxRepo;


//## sync place order
//1. order validate
//2. order validate thong tin co ban orderItem (quantity, product_id) call product-service
//3. lock quantity của sản phẩm (cụ thể: stock - quantity) => gọi qua product-service
//4. save Order


//## async place order
//1. validate order
//2. call product-service to validate
//3. publish event (message kafka) => order_created
//4. product-service consume message kafka -> block product
//5. product-service publish message kafka -> produc_locked
//6. order-service consume message kafka (product_locked) => chuyen trang thai order

    @Override
    @Transactional
    public OrderRes create(CreateOrderReq req) {
        // get list product id from created order
        List<String> ids = req.getItems().stream().map(OrderItemReq::getProductId)
                .distinct()
                .toList();

        // call product service qua webClient
        // get product with ids
        List<ProductDto> products = productClient.getProductByIds(new ProductFilter(ids));
        log.info("Get list product from product service: [{}]", products);

        Map<String, ProductDto> map = new HashMap<>();
        products.forEach(product -> {
            map.put(product.getId(), product);
        });
        log.info("Create order");
        OrderEntity order = new OrderEntity();
        order.setCustomerId(req.getCustomerId());

        BigDecimal amount = BigDecimal.ZERO;
        order.setAmount(amount);
        order.setPaymentMethod(req.getPaymentMethod());
        order.setStatus(StatusOrder.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);

        OrderEntity savedOrder = repo.save(order);
        // list order item
        List<OrderItemEntity> orderItems = new ArrayList<>();

        // get list item from input
        List<OrderItemReq> items = req.getItems();

        for(OrderItemReq item : items) {
            // get(key) -> value
            ProductDto product = map.get(item.getProductId());
            if(product == null) {
                throw new BusinessException("Product not found");
            }

            OrderItemEntity orderItem = new OrderItemEntity();
            orderItem.setProductId(product.getId());
            orderItem.setOrder(order);

            // validate
            if(item.getQuantity() == null || item.getQuantity() == 0) {
                throw new BusinessException("Quantity not set");
            }

            if(item.getQuantity() > product.getQuantityInStock()) {
                throw new BusinessException("Product "+ item.getProductId() +" not enough quantity");
            }
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(product.getPrice());

            // logic pricing
            amount = amount.add(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            orderItems.add(orderItem);

        }
        savedOrder.setAmount(amount);
        savedOrder.setOrderItems(orderItems);

        itemRepo.saveAll(orderItems);
        OrderEntity orderEntity = repo.save(order);

        // 1. sync: call product service qua webclient to lock product
        // lock product
        //productClient.lockProduct(new LockProductReq(items));

        // 2. async: create message and publish to kafka
        OrderCreatedEvent orderCreatedEvent = orderMapper.toEvent(orderEntity);
        orderCreatedEvent.setOrderItems(items);
//        kafkaTemplate.send("order_created", orderCreatedEvent);

        // 3. outbox pattern: use debezium (change data capture)
        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setPayload(objectMapper.writeValueAsString(orderCreatedEvent));
        outboxEvent.setEventType("OrderCreated");
        outboxEvent.setAggregateId(orderEntity.getId());
        outboxEvent.setAggregateType("order");
        outboxEvent.setCreatedAt(LocalDateTime.now());
        outboxRepo.save(outboxEvent);
        log.info("Order Created Event: {}", outboxEvent);

        //log.info("Published new order event to kafka");
        return orderMapper.fromOrderEntity(orderEntity);
    }

    @Override
    @Transactional
    public void updateOrderStatus(String orderId, StatusOrder status) {
        OrderEntity order = repo.findById(orderId)
                .orElseThrow(() -> new BusinessException("Order not found"));
        order.setStatus(status);
        log.info("Updated order status to {}", order.getStatus());
        repo.save(order);
    }


}
