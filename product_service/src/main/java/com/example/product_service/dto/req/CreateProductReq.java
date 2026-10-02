package com.example.product_service.dto.req;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class CreateProductReq {
    private String productName;
    private String description;
    private String categoryId;
    private BigDecimal price;
    private Integer quantityInStock;
}
