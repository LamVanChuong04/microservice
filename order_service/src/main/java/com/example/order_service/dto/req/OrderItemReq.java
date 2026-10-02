package com.example.order_service.dto.req;

import lombok.Data;

@Data
public class OrderItemReq {
    private String productId;
    private Integer quantity;
}
