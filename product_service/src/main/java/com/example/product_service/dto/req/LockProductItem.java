package com.example.product_service.dto.req;

import lombok.Data;

@Data
public class LockProductItem {
    private String productId;
    private Integer quantity;
}
