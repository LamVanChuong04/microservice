package com.example.order_service.dto.req;

import com.example.order_service.enums.PaymentMethod;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateOrderReq {
    private String customerId;
    private PaymentMethod paymentMethod;
    private List<OrderItemReq> items;


}
