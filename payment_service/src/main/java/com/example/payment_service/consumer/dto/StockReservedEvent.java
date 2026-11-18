package com.example.payment_service.consumer.dto;


import com.example.payment_service.enums.PaymentMethod;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StockReservedEvent {
    private String orderId;
    private String userId;
    private PaymentMethod method;
    private BigDecimal amount;
    private List<OrderItem> items;
}
