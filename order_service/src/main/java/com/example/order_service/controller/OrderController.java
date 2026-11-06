package com.example.order_service.controller;

import com.example.order_service.common.BaseResponse;
import com.example.order_service.common.CustomHeaders;
import com.example.order_service.dto.req.CreateOrderReq;
import com.example.order_service.dto.res.OrderRes;
import com.example.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService service;

    @PostMapping()
    public ResponseEntity<BaseResponse<OrderRes>> placeOrder(@RequestHeader(CustomHeaders.X_AUTH_USER_ID) String userId
            ,@RequestBody CreateOrderReq req) {
        return new ResponseEntity<>(BaseResponse.ofSuccess(service.create(userId,req)), HttpStatus.CREATED);
    }

    @GetMapping()
    public ResponseEntity<BaseResponse<List<OrderRes>>> getAllOrder(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDes
    )
    {
        Sort sort = sortBy.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        return new ResponseEntity<>(BaseResponse.ofSuccess(service.getAllOrder(pageable)), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<OrderRes>> getById(@PathVariable(name = "id") String id)
    {
        return new ResponseEntity<>(BaseResponse.ofSuccess(service.getById(id)), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<String>> delete(@PathVariable(name = "id") String id)
    {
        return new ResponseEntity<>(BaseResponse.ofSuccess(service.delete(id)), HttpStatus.OK);
    }

}
