package com.example.order_service.service;

import com.example.order_service.dto.req.CreateOrderReq;
import com.example.order_service.dto.res.OrderRes;
import com.example.order_service.enums.StatusOrder;

public interface OrderService {
    OrderRes create(CreateOrderReq req);
    void updateOrderStatus(String orderId, StatusOrder status);

}
