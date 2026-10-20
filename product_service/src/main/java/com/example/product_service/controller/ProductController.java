package com.example.product_service.controller;

import com.example.product_service.common.BaseResponse;
import com.example.product_service.dto.req.CreateProductReq;
import com.example.product_service.dto.req.LockProductReq;
import com.example.product_service.dto.req.ProductFilter;
import com.example.product_service.dto.res.ProductRes;
import com.example.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService service;

    @PostMapping("/create")
    public ResponseEntity<BaseResponse<ProductRes>> addProduct(@RequestBody CreateProductReq product) {
        return new ResponseEntity<>(BaseResponse.ofSuccess(service.create(product)), HttpStatus.CREATED);
    }

    @PostMapping("/search")
    public ResponseEntity<BaseResponse<List<ProductRes>>> search(@RequestBody ProductFilter productFilter) {
        List<ProductRes> products = service.search(productFilter);
        return new ResponseEntity<>(BaseResponse.ofSuccess(products), HttpStatus.OK);
    }

    @PutMapping("/lock")
    public ResponseEntity<BaseResponse<Boolean>> lockProduct(@RequestBody LockProductReq product) {
        service.lock(product);
        return new ResponseEntity<>(BaseResponse.ofSuccess(true), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ProductRes>> getProduct(@PathVariable String id)
    {
        return new ResponseEntity<>(BaseResponse.ofSuccess(service.getProduct(id)), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<ProductRes>> updateProduct(@PathVariable String id,
                                                                  @RequestBody CreateProductReq req)
    {
        return new ResponseEntity<>(BaseResponse.ofSuccess(service.update(id, req)), HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<ProductRes>> delete(@PathVariable String id)
    {
        service.delete(id);
        return new ResponseEntity<>(BaseResponse.ofSuccess("Deleted product sucessfull"), HttpStatus.OK);
    }
}
