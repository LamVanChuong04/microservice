package com.example.product_service.consumer.dto;



import com.example.product_service.enums.PaymentMethod;
import com.example.product_service.enums.PaymentStatus;
import com.example.product_service.enums.StatusOrder;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class Order {
    private String id;
    private String customerId;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private BigDecimal amount;
    private StatusOrder status;
}
