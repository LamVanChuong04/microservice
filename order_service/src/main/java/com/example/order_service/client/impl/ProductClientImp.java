package com.example.order_service.client.impl;

import com.example.order_service.client.ProductClient;
import com.example.order_service.common.BaseResponse;
import com.example.order_service.dto.client.LockProductReq;
import com.example.order_service.dto.client.ProductDto;
import com.example.order_service.dto.req.ProductFilter;
import com.example.order_service.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
@Component
@RequiredArgsConstructor
public class ProductClientImp implements ProductClient {
    private final WebClient.Builder webClient;

    @Override
    public List<ProductDto> getProductByIds(ProductFilter filter) {
        WebClient.Builder webClientBuilder = WebClient.builder();
        BaseResponse<List<ProductDto>> response = webClientBuilder.build()
                .post()
                .uri("http://localhost:8081/api/v1/products/search")
                .bodyValue(filter)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<BaseResponse<List<ProductDto>>>() {
                })
                .block();
        if (response == null && response.getData() == null) {
            throw new BusinessException("Khong tim thay san pham.");
        }
        return response.getData();
    }

    @Override
    public void lockProduct(LockProductReq req) {
        WebClient.Builder webClientBuilder = WebClient.builder();
        BaseResponse<Boolean> response = webClientBuilder.build()
                .put()
                .uri("http://localhost:8081/api/v1/products/lock")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<BaseResponse<Boolean>>() {
                })
                .block();
        if(response.equals(false))
            throw new BusinessException("Khong the lock product.");
    }
}
