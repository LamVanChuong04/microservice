package com.example.order_service.dto.res;

import com.example.order_service.enums.PaymentMethod;
import com.example.order_service.enums.PaymentStatus;
import com.example.order_service.enums.StatusOrder;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderRes {
    private String id;
    private String customerId;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private BigDecimal amount;
    private StatusOrder status;
}
