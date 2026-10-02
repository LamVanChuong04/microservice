package com.example.product_service.service;

import com.example.product_service.dto.req.CreateCategoryReq;
import com.example.product_service.dto.res.CategoryRes;

public interface CategoryService {
    CategoryRes create(CreateCategoryReq req);
}
