package com.example.product_service.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentFailedEvent {
    private String orderId;
    private List<OrderItem> items;
}
