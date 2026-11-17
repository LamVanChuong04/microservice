package com.example.order_service.events;

import com.example.order_service.dto.req.OrderItemReq;
import com.example.order_service.dto.res.OrderItemRes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCancelEvent {
    private List<OrderItemReq> orderItems;
}
