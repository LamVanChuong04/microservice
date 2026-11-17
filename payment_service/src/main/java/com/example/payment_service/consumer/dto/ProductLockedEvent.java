package com.example.payment_service.consumer.dto;

import com.example.payment_service.enums.PaymentMethod;
import lombok.*;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductLockedEvent {
    private String orderId;
    private String userId;
    private PaymentMethod method;
    private BigDecimal amount;
}
