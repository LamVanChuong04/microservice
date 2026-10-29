package com.example.order_service.service;

import com.example.order_service.dto.req.CreateOrderReq;
import com.example.order_service.dto.res.OrderRes;
import com.example.order_service.enums.StatusOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    OrderRes create(CreateOrderReq req);
    void updateOrderStatus(String orderId, StatusOrder status);
    OrderRes getOrderById(String id);
    Page<OrderRes> getAllOrder(Pageable pageable);
}
