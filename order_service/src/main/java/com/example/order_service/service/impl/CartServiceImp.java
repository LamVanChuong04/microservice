package com.example.order_service.service.impl;

import com.example.order_service.client.ProductClient;
import com.example.order_service.dto.client.ProductDto;
import com.example.order_service.dto.req.AddToCart;
import com.example.order_service.dto.req.OrderItemReq;
import com.example.order_service.dto.req.ProductFilter;
import com.example.order_service.dto.res.CartItemRes;
import com.example.order_service.exception.BusinessException;
import com.example.order_service.service.CartService;
import com.example.order_service.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImp implements CartService {
    private final RedisService redisService;
    private final ProductClient productClient;

    @Override
    public void addProductToCart(AddToCart req) {
        log.info("add product to cart with user id {} items {}", req.getUserId(), req.getItem());

        ProductDto product = productClient.getProductById(req.getItem().getProductId());
        if (product == null) {
            throw new BusinessException("Không tìm thấy sản phẩm.");
        }

        String fieldKey = "items:" + product.getId();

        Integer currentQuantity = (Integer) redisService.hashGet(req.getUserId(), fieldKey);
        int updateQuantity = (currentQuantity == null ? 0 : currentQuantity) + req.getItem().getQuantity();

        redisService.hashSet(req.getUserId(), fieldKey, updateQuantity);
        redisService.setWithTtl(req.getUserId(), 30, TimeUnit.DAYS); // TTL cho toàn bộ giỏ hàng
    }

    @Override
    public void updateProductInCart(String userId, List<OrderItemReq> items) {

    }

    @Override
    public void deleteProductInCart(AddToCart req) {
        String fieldKey = "items:" + req.getItem().getProductId();
        redisService.delete(req.getUserId(), fieldKey);
    }

    @Override
    public List<CartItemRes> getProductsInCart(String userId) {
        Map<String, Object> map = redisService.getField(userId);
        List<CartItemRes> items = new ArrayList<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String productId = entry.getKey().replace("items:", "");
            Integer quantity = Integer.parseInt(entry.getValue().toString());
            ProductDto product = productClient.getProductById(productId);
            items.add(new CartItemRes(product, quantity));
        }
        return items;
    }
}
