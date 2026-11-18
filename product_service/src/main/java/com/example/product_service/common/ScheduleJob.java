package com.example.product_service.common;

import com.example.product_service.entity.OutboxEvent;
import com.example.product_service.events.StockReleasedEvent;
import com.example.product_service.events.StockReservedEvent;
import com.example.product_service.service.OutboxEventService;
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
    
    // CÁCH 1: Row-by-row
    // Gửi từng message, nhận ACK, update PUBLISHED ngay lập tức.
    //
    // + Failure window nhỏ: nếu crash giữa chừng, chỉ row đang gửi bị retry
    // + Đơn giản, dễ debug: fail ở row nào, log rõ row đó
    // - Nhiều DB round trip: N messages = N lần UPDATE riêng lẻ
    // - Throughput thấp hơn batch khi có nhiều PENDING rows
    // =========================================================

    // polling job
    @Scheduled(fixedDelay = 1000)
    public void publish()
    {
        publishRowByRow();
    }
    private void publishRowByRow()
    {
        List<OutboxEvent> events = service.findPendingBatch(BATCH_SIZE);
        log.info("sau query");
        for (OutboxEvent event : events)
        {
            log.info("sau for");
            try {
                if("stock_reserved_event".equals(event.getEventType()))
                {
                    log.info("stock_reversed_event");
                    StockReservedEvent order = mapper.readValue(event.getPayload(), StockReservedEvent.class);
                    template.send("product_locked", order);
                    service.markPublisled(event.getId(), LocalDateTime.now());
                }
                else if ("stock_released_event".equals(event.getEventType()))
                {
                    log.info("stock_released_event");
                    StockReleasedEvent sre = mapper.readValue(event.getPayload(), StockReleasedEvent.class);
                    template.send("stock_released", sre);
                    service.markPublisled(event.getId(), LocalDateTime.now());
                }


                log.debug("OutboxPublisher [row-by-row]: published eventId={} aggregateType={}",
                        event.getId(), event.getAggregateType());
            }
            catch (Exception e)
            {
                log.error("OutboxPublisher [row-by-row]: failed eventId={}, will retry next cycle",
                        event.getId(), e);
            }
        }
    }

}
