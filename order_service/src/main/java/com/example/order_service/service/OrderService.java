package com.example.order_service.service;

import com.example.order_service.dto.req.CreateOrderReq;
import com.example.order_service.dto.res.OrderRes;

public interface OrderService {
    OrderRes create(CreateOrderReq req);
    void updateOrderStatus(String orderId);
}
