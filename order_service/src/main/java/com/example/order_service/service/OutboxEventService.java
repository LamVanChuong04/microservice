package com.example.order_service.service;


import com.example.order_service.entity.OutboxEvent;

import java.time.LocalDateTime;
import java.util.List;


public interface OutboxEventService {
    void markPublished(Long id, LocalDateTime publishedAt);
    void clearOutboxEvent();
    List<OutboxEvent> findPendingBatch(int limit);
}
