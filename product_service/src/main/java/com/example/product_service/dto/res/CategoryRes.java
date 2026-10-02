package com.example.product_service.dto.res;

import lombok.Data;

@Data
public class CategoryRes {
    private String id;
    private String name;
    private String parentId;
}
