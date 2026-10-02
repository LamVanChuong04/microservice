package com.example.product_service.dto.req;

import lombok.Data;

import java.util.List;
@Data
public class LockProductReq {
    private List<LockProductItem> items;
}
