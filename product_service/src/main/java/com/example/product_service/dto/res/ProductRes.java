package com.example.product_service.dto.res;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
@Data
public class ProductRes implements Serializable {
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
