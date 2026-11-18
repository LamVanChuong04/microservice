package com.example.payment_service.service.imp;

import com.example.payment_service.entity.OutboxEvent;
import com.example.payment_service.repository.OutboxEventRepository;
import com.example.payment_service.service.OutboxEventService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OutboxEventServiceImp implements OutboxEventService {
    private final OutboxEventRepository repo;
    private final KafkaTemplate<String, Object> template;



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
    public void clearOutboxEvent() {
        repo.deleteEventAfter24h();
    }
}
