package com.example.product_service.events;

import com.example.product_service.enums.PaymentMethod;
import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderDto {
    private String orderId;
    private String userId;
    private PaymentMethod method;
    private BigDecimal amount;
}
