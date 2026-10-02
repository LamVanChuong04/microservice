package com.example.order_service.service.impl;

import com.example.order_service.client.ProductClient;
import com.example.order_service.dto.client.LockProductReq;
import com.example.order_service.dto.client.ProductDto;
import com.example.order_service.dto.req.CreateOrderReq;
import com.example.order_service.dto.req.OrderItemReq;
import com.example.order_service.dto.req.ProductFilter;
import com.example.order_service.dto.res.OrderRes;
import com.example.order_service.entity.OrderEntity;
import com.example.order_service.entity.OrderItemEntity;
import com.example.order_service.enums.PaymentStatus;
import com.example.order_service.enums.StatusOrder;
import com.example.order_service.exception.BusinessException;
import com.example.order_service.mapper.OrderMapper;
import com.example.order_service.repository.OrderItemRepository;
import com.example.order_service.repository.OrderRepository;
import com.example.order_service.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderServiceImp implements OrderService {
    private final OrderMapper orderMapper;
    private final OrderRepository repo;
    private final OrderItemRepository itemRepo;
    private final ProductClient productClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    @Transactional
    public OrderRes create(CreateOrderReq req) {
        List<String> ids = req.getItems().stream().map(OrderItemReq::getProductId)
                .distinct()
                .toList();

        List<ProductDto> products = productClient.getProductByIds(new ProductFilter(ids));

        Map<String, ProductDto> map = new HashMap<>();
        products.forEach(product -> {
            map.put(product.getId(), product);
        });

        OrderEntity order = new OrderEntity();
        order.setCustomerId(req.getCustomerId());

        BigDecimal amount = BigDecimal.ZERO;
        order.setAmount(amount);
        order.setPaymentMethod(req.getPaymentMethod());
        order.setStatus(StatusOrder.PENDING);
        order.setPaymentStatus(PaymentStatus.SUCCESS);

        OrderEntity savedOrder = repo.save(order);
        // list order item
        List<OrderItemEntity> orderItems = new ArrayList<>();

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
        // lock product
        productClient.lockProduct(new LockProductReq(items));
        kafkaTemplate.send("created_order", orderMapper.fromOrderEntity(orderEntity));
        return orderMapper.fromOrderEntity(orderEntity);
    }
}
