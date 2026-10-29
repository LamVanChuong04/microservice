package com.example.order_service.repository;

import com.example.order_service.entity.OutboxEvent;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM outbox_event o WHERE o.created_at < NOW() - INTERVAL 24 hour", nativeQuery = true)
    void deleteEventAfter24h();
}
