package com.example.order_service.common;

import com.example.order_service.entity.OutboxEvent;
import com.example.order_service.events.OrderCreatedEvent;
import com.example.order_service.repository.OutboxEventRepository;
import com.example.order_service.service.OutboxEventService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class ScheduleJob {
    private static final int BATCH_SIZE = 500;
    private final OutboxEventService service;
    private final ObjectMapper mapper;
    private KafkaTemplate<String, Object> template;


    @Scheduled(cron = "0 0/5 * * * ?")
    @SchedulerLock(
            name = "clearOutboxEvent",
            lockAtLeastFor = "1m",
            lockAtMostFor = "15m"
    )
    public void clearEvent(){
        service.clearOutboxEvent();
    }

    
    @Scheduled(fixedDelay = 1000)
    public void publish()
    {
        publishRowByRow();
    }

    // polling job
    private void publishRowByRow(){
        List<OutboxEvent> events = service.findPendingBatch(BATCH_SIZE);

        for(OutboxEvent event : events)
        {
            try{
                log.info("get event: {}", event.getId());
                OrderCreatedEvent order = mapper.readValue(event.getPayload(), OrderCreatedEvent.class);
                // publish mess to broker
                template.send("order_created", order);
                // update status
                service.markPublished(event.getId(), LocalDateTime.now());
                log.debug("OutboxPublisher [row-by-row]: published eventId={} token={}",
                        event.getId(), event.getAggregateId());
            }
            catch (Exception e)
            {
                // Kafka fail hoặc ACK timeout → KHÔNG update status → cycle sau tự retry
                log.error("OutboxPublisher [row-by-row]: failed eventId={}, will retry next cycle",
                        event.getId(), e);
            }
        }
    }
}
