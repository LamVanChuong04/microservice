package com.example.product_service.service;

import com.example.product_service.dto.req.CreateProductReq;
import com.example.product_service.dto.req.LockProductReq;
import com.example.product_service.dto.req.ProductFilter;
import com.example.product_service.dto.res.ProductRes;

import java.util.List;

public interface ProductService {
    ProductRes create(CreateProductReq req);
    List<ProductRes> search(ProductFilter productFilter);
    void lock(LockProductReq req);
}
