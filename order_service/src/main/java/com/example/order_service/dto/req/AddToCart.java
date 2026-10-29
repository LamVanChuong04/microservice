package com.example.order_service.dto.req;

import lombok.Data;

@Data
public class AddToCart {
    private String userId;
    private OrderItemReq item;
}
