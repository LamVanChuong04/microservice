package com.example.product_service.consumer.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderCancelEvent {
    private List<OrderItem> orderItems;
}
