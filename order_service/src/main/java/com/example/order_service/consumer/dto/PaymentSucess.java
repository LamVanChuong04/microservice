package com.example.order_service.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentSucess {
    private String orderId;
}
