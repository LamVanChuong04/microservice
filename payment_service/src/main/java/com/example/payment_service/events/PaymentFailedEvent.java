package com.example.payment_service.events;

import com.example.payment_service.consumer.dto.OrderItem;
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
