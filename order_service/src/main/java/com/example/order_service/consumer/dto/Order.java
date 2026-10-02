package com.example.order_service.consumer.dto;

import com.example.order_service.enums.PaymentMethod;
import com.example.order_service.enums.PaymentStatus;
import com.example.order_service.enums.StatusOrder;

import java.math.BigDecimal;

public class Order {
    private String id;
    private String customerId;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private BigDecimal amount;
    private StatusOrder status;
}
