package com.example.payment_service.service;

import com.example.payment_service.entity.OutboxEvent;

import java.time.LocalDateTime;
import java.util.List;

public interface OutboxEventService {
    void markPublisled(Long id, LocalDateTime publishedAt);
    List<OutboxEvent> findPendingBatch(int limit);
    void clearOutboxEvent();
}
