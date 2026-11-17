package com.example.order_service.repository;

import com.example.order_service.entity.OutboxEvent;
import jakarta.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM outbox_event o WHERE o.created_at < NOW() - INTERVAL 24 hour", nativeQuery = true)
    void deleteEventAfter24h();

    @Modifying
    @Query(value = "UPDATE OutboxEvent o SET o.status = 1, o.publishedAt = :publishedAt WHERE o.id = :id")
    int markPublishededById(@Param("id") Long id, @Param("publishedAt") LocalDateTime publishedAt);

    @Query(value = "SELECT o FROM OutboxEvent o WHERE o.status = 0 ORDER BY o.createdAt asc")
    List<OutboxEvent> findPending(Pageable pageable);
}
