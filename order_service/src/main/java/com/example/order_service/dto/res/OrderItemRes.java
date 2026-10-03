package com.example.order_service.dto.res;

import lombok.Data;

@Data
public class OrderItemRes {
    private String productId;
    private Integer quantity;
}
