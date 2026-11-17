package com.example.product_service.service.impl;

import com.example.product_service.entity.OutboxEvent;
import com.example.product_service.repository.OutboxEventRepository;
import com.example.product_service.service.OutboxEventService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboxEventServiceImp implements OutboxEventService {
    private final OutboxEventRepository repo;
    @Override
    @Transactional
    public void markPublisled(Long id, LocalDateTime publishedAt) {
        repo.markPublished(id, publishedAt);
    }

    @Override
    public List<OutboxEvent> findPendingBatch(int limit) {
        return repo.findPending(PageRequest.of(0, limit));
    }

    @Override
    @Transactional
    public void clearOutboxEvent() {
        repo.deleteEventAfter24h();
    }
}
