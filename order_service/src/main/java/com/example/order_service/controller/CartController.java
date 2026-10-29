package com.example.order_service.controller;

import com.example.order_service.common.BaseResponse;
import com.example.order_service.dto.req.AddToCart;
import com.example.order_service.dto.req.OrderItemReq;
import com.example.order_service.dto.res.CartItemRes;
import com.example.order_service.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService service;

    @PostMapping()
    public ResponseEntity<BaseResponse<String>> addToCart(@RequestBody AddToCart req)
    {
        service.addProductToCart(req);
        return new ResponseEntity<>(BaseResponse.ofSuccess("add product to cart success"), HttpStatus.OK);
    }

    @DeleteMapping()
    public ResponseEntity<BaseResponse<String>> delete(@RequestBody AddToCart req)
    {
        service.deleteProductInCart(req);
        return new ResponseEntity<>(BaseResponse.ofSuccess("deleted product in cart success"), HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<BaseResponse<List<CartItemRes>>> getCart(@PathVariable String userId) {
        List<CartItemRes> items = service.getProductsInCart(userId);
        return ResponseEntity.ok(BaseResponse.ofSuccess(items));
    }

}

