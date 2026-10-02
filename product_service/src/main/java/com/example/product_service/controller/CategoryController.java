package com.example.product_service.controller;

import com.example.product_service.common.BaseResponse;
import com.example.product_service.dto.req.CreateCategoryReq;
import com.example.product_service.dto.res.CategoryRes;
import com.example.product_service.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService service;

    @PostMapping("/category")
    public ResponseEntity<BaseResponse<CategoryRes>> addCategory(@RequestBody CreateCategoryReq category) {
        return new ResponseEntity<>(BaseResponse.ofSuccess(service.create(category)), HttpStatus.CREATED);
    }
}
