package com.example.order_service.events;

import com.example.order_service.dto.req.OrderItemReq;
import com.example.order_service.dto.res.OrderRes;
import lombok.Data;

import java.util.List;
@Data
public class OrderCreatedEvent extends OrderRes {
    private List<OrderItemReq> orderItems;
}
