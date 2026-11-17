package com.example.order_service.producer;

import com.example.order_service.events.OrderCancelEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventProducer {
    private final KafkaTemplate<String, Object> template;

    public void handlePaymentFailed(OrderCancelEvent event)
    {
        template.send("order_cancel", event);
    }
}
