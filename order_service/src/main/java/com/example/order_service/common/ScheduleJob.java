package com.example.order_service.common;

import com.example.order_service.repository.OutboxEventRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ScheduleJob {
    private final OutboxEventRepository repo;

    @Scheduled(cron = "0 0 8 * * *")
    public void clearOutboxEvent(){
        repo.deleteEventAfter24h();
    }
}
