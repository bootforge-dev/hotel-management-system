package com.bootforge.hbp.paymentservice.kafka.producer;

import com.bootforge.hbp.common.dto.booking.outbox.OutboxStatus;
import com.bootforge.hbp.common.event.PaymentFailedEvent;
import com.bootforge.hbp.common.event.PaymentSuccessEvent;
import com.bootforge.hbp.paymentservice.entity.OutboxEvent;
import com.bootforge.hbp.paymentservice.repository.OutboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jcajce.provider.symmetric.CAST5;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxEventPublisher {

    private static final String PAYMENT_SUCCESS = "payment-success";
    private static final String PAYMENT_FAILED = "payment-failed";

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
            switch (event.getEventType()) {
                case "PAYMENT_SUCCESS" -> {
                    PaymentSuccessEvent successEvent =
                            objectMapper.readValue(
                                    event.getPayload(),
                                    PaymentSuccessEvent.class
                            );
                    send(
                            PAYMENT_SUCCESS,
                            event,
                            successEvent
                    );
                }
                case "PAYMENT_FAILED" -> {
                    PaymentFailedEvent failedEvent =
                            objectMapper.readValue(
                                    event.getPayload(),
                                    PaymentFailedEvent.class
                            );
                    send(
                            PAYMENT_FAILED,
                            event,
                            failedEvent
                    );
                }
                default -> {
                    log.warn("Unknown outbox event type: {}", event.getEventType());
                }
            }
        } catch (Exception e) {
            markFailed(event, e);
        }
    }

    private void send(String topic, OutboxEvent outboxEvent, Object payload) {
        kafkaTemplate.send(topic, outboxEvent.getAggregateId(), payload)
                .whenComplete((result, exception) -> {
                    if (exception == null) {
                        outboxEvent.setStatus(OutboxStatus.PUBLISHED);
                        outboxEvent.setPublishedAt(LocalDateTime.now());
                        outboxEvent.setLastError(null);

                        outboxRepository.save(outboxEvent);
                    } else {
                        markFailed(outboxEvent, exception);
                    }
                });
    }

    private void markFailed(OutboxEvent event, Throwable exception) {
        event.setRetryCount(event.getRetryCount() + 1);
        event.setLastError(exception.getMessage());

        outboxRepository.save(event);
        log.error("Failed to publish payment event. eventId={}", event.getEventId(), exception);
    }


}
