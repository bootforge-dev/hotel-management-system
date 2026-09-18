package com.bootforge.hbp.bookingservice.kafka.producer;

import com.bootforge.hbp.bookingservice.entity.OutboxEvent;
import com.bootforge.hbp.bookingservice.repository.OutboxEventRepository;
import com.bootforge.hbp.common.dto.booking.outbox.OutboxStatus;
import com.bootforge.hbp.common.event.BookingCreatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxEventPublisher {

    private static final String BOOKING_CREATED = "booking-created";
    private final OutboxEventRepository outboxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 1000)
    public void publishPendingEvents() {
        List<OutboxEvent> events = outboxRepository.findTop100ByStatusOrderByCreatedAtAsc(
                OutboxStatus.PENDING);
        for (OutboxEvent event : events) {
            publish(event);
        }
    }

    private void publish(OutboxEvent event) {
        try {
            BookingCreatedEvent bookingEvent = objectMapper.readValue(event.getPayload(), BookingCreatedEvent.class);

            kafkaTemplate.send(BOOKING_CREATED, event.getAggregateId(), bookingEvent)
                    .whenComplete((result, exception) -> {
                        if (exception == null) {
                            event.setStatus(OutboxStatus.PUBLISHED);
                            event.setPublishedAt(LocalDateTime.now());
                            event.setLastError(null);

                            outboxRepository.save(event);

                            log.info("Booking outbox published. eventId:{}", event.getEventId());
                        } else {
                            event.setRetryCount(event.getRetryCount() + 1);
                            event.setLastError(exception.getMessage());

                            outboxRepository.save(event);

                            log.info("Failed to  publish booking outbox event. eventId:{}", event.getEventId(), exception);
                        }
                    });

        } catch (Exception e) {
            event.setRetryCount(event.getRetryCount()+1);
            event.setLastError(e.getMessage());

            outboxRepository.save(event);

            log.info("Failed to  serialize booking outbox event. eventId:{}", event.getEventId(), e);
        }
    }


}
