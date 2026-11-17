package com.example.product_service.service;

import com.example.product_service.entity.OutboxEvent;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface OutboxEventService {
    void markPublisled(Long id, LocalDateTime publishedAt);
    List<OutboxEvent> findPendingBatch(int limit);
    void clearOutboxEvent();
}
