package com.example.order_service.dto.client;

import com.example.order_service.dto.req.OrderItemReq;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LockProductReq {
    @Valid
    private List<OrderItemReq> items;
}
