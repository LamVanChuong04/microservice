package com.example.order_service.client;

import com.example.order_service.dto.client.LockProductReq;
import com.example.order_service.dto.client.ProductDto;
import com.example.order_service.dto.req.ProductFilter;

import java.util.List;

public interface ProductClient {
    List<ProductDto> getProductByIds(ProductFilter filter);
    void lockProduct(LockProductReq re);
}
