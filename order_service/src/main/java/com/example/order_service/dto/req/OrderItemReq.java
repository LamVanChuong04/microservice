package com.example.order_service.dto.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class OrderItemReq {
    @NotEmpty(message = "Product id is not empty")
    private String productId;
    @Min(1)
    @NotEmpty(message = "Product quantity is not empty")
    private Integer quantity;
}
