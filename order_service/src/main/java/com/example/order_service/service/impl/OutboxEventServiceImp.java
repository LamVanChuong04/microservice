package com.example.order_service.service.impl;

import com.example.order_service.entity.OutboxEvent;
import com.example.order_service.repository.OutboxEventRepository;
import com.example.order_service.service.OutboxEventService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboxEventServiceImp implements OutboxEventService {
    private final OutboxEventRepository repo;

    @Override
    @Transactional
    public void markPublished(Long id, LocalDateTime publishedAt) {
        repo.markPublishededById(id, publishedAt);
    }

    @Override
    public void clearOutboxEvent() {
        repo.deleteEventAfter24h();
    }

    @Override
    public List<OutboxEvent> findPendingBatch(int limit) {
        return repo.findPending(PageRequest.of(0, limit));
    }
}
