package com.example.payment_service.common;


import com.example.payment_service.repository.OutboxEventRepository;
import lombok.AllArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ScheduleJob {
    private final OutboxEventRepository repo;

    @Scheduled(cron = "0 0/5 * * * ?")
    @SchedulerLock(
            name = "clearOutboxEvent",
            lockAtLeastFor = "1m",
            lockAtMostFor = "15m"
    )
    public void clearOutboxEvent(){
        repo.deleteEventAfter24h();
    }
}
