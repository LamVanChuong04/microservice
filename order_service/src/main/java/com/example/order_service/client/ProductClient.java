package com.example.order_service.client;

import com.example.order_service.dto.client.LockProductReq;
import com.example.order_service.dto.client.ProductDto;
import com.example.order_service.dto.req.OrderItemReq;
import com.example.order_service.dto.req.ProductFilter;

import java.util.List;

public interface ProductClient {
    List<ProductDto> getProductByIds(ProductFilter filter);
    ProductDto getProductById(String productId);
    void lockProduct(LockProductReq re);
}
