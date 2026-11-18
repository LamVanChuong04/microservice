package com.example.payment_service.common;


import com.example.payment_service.entity.OutboxEvent;
import com.example.payment_service.events.PaymentCompletedEvent;
import com.example.payment_service.events.PaymentFailedEvent;
import com.example.payment_service.service.OutboxEventService;
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
    private final OutboxEventService service;
    private final static int BATCH_SIZE = 500;
    private final KafkaTemplate<String, Object> template;
    private final ObjectMapper mapper;

    @Scheduled(cron = "0 0/5 * * * ?")
    @SchedulerLock(
            name = "clearOutboxEvent",
            lockAtLeastFor = "1m",
            lockAtMostFor = "15m"
    )
    public void clearOutboxEvent(){
        service.clearOutboxEvent();
    }

    @Scheduled(fixedDelay = 1000)
    public void publish()
    {
        publishedRowByRow();
    }


    private void publishedRowByRow()
    {
        List<OutboxEvent> events = service.findPendingBatch(BATCH_SIZE);
        for (OutboxEvent event : events) {

            try {

                // publish to broker
                if ("payment_sucess".equals(event.getAggregateType())) {
                    PaymentCompletedEvent pce = mapper.readValue(event.getPayload(), PaymentCompletedEvent.class);
                    template.send("payment_sucess", pce);
                } else if ("payment_failed".equals(event.getAggregateType())) {
                    PaymentFailedEvent pfe = mapper.readValue(event.getPayload(), PaymentFailedEvent.class);
                    template.send("payment_failed", pfe);
                }
                // update status
                service.markPublisled(event.getId(), LocalDateTime.now());

                log.debug("OutboxPublisher [row-by-row]: published eventId={} aggregatedType={}",
                        event.getId(), event.getAggregateType());
            }
            catch (Exception e)
            {
                log.error("OutboxPublisher [row-by-row]: published eventId={} aggregatedType={}",
                        event.getId(), event.getAggregateType());
            }
        }

    }
}
