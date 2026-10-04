package com.example.order_service.controller;

import com.example.order_service.common.BaseResponse;
import com.example.order_service.dto.req.CreateOrderReq;
import com.example.order_service.dto.res.OrderRes;
import com.example.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService service;

    @PostMapping()
    public ResponseEntity<BaseResponse<OrderRes>> placeOrder(@RequestBody CreateOrderReq req) {
        return new ResponseEntity<>(BaseResponse.ofSuccess(service.create1(req)), HttpStatus.CREATED);
    }
}
