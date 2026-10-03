package com.example.product_service.consumer.dto;

import lombok.Data;

@Data
public class OrderItem {
    private String productId;
    private Integer quantity;
}
