package com.example.order_service.service;

import com.example.order_service.dto.client.ProductDto;
import com.example.order_service.dto.req.AddToCart;
import com.example.order_service.dto.req.OrderItemReq;
import com.example.order_service.dto.res.CartItemRes;

import java.util.List;
import java.util.Map;

public interface CartService {
    void addProductToCart(AddToCart req);
    void updateProductInCart(String userId, List<OrderItemReq> items);
    void deleteProductInCart(AddToCart req);
    List<CartItemRes> getProductsInCart(String userId);
}
