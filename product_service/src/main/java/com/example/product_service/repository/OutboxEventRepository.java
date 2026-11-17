package com.example.product_service.repository;

import com.example.product_service.entity.OutboxEvent;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {
    @Modifying
    @Query(value = "DELETE FROM outbox_event o WHERE o.created_at < NOW() - INTERVAL 24 hour", nativeQuery = true)
    void deleteEventAfter24h();

    @Query(value = "SELECT o FROM OutboxEvent o WHERE o.status = 0 order by o.createdAt asc")
    List<OutboxEvent> findPending(Pageable pageable);

    @Modifying
    @Query(value = "update OutboxEvent o set o.status = 1, o.publishedAt = :publishedAt where o.id = :id")
    int markPublished(@Param("id") Long id, @Param("publishedAt")LocalDateTime publishedAt);
}
