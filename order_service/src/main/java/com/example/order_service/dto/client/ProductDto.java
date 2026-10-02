package com.example.order_service.dto.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class ProductDto {
    private String id;
    @JsonProperty("product_name")
    private String productName;
    @JsonProperty("quantity_in_stock")
    private Integer quantityInStock;
    private BigDecimal price;
    @JsonProperty("category_id")
    private String categoryId;
    private String description;
}
