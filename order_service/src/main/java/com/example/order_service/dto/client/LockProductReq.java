package com.example.order_service.dto.client;

import com.example.order_service.dto.req.OrderItemReq;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LockProductReq {
    private List<OrderItemReq> items;
}
