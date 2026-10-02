package com.example.product_service.dto.req;

import lombok.Data;

@Data
public class CreateCategoryReq {
    private String name;
    private String parentId;
}
