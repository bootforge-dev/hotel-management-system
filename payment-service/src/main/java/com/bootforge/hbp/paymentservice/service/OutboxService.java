package com.bootforge.hbp.paymentservice.service;

import com.bootforge.hbp.common.dto.booking.outbox.OutboxStatus;
import com.bootforge.hbp.common.event.PaymentFailedEvent;
import com.bootforge.hbp.common.event.PaymentSuccessEvent;
import com.bootforge.hbp.paymentservice.entity.OutboxEvent;
import com.bootforge.hbp.paymentservice.repository.OutboxEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxService {
    private final OutboxEventRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public void savePaymentSuccessEvent(PaymentSuccessEvent event){
        save(
                event.eventId(),
                String.valueOf(event.bookingId()),
                "PAYMENT_SUCCESS",
                event
        );
    }

    public void savePaymentFailedEvent(PaymentFailedEvent event){
        save(
                event.eventId(),
                String.valueOf(event.bookingId()),
                "PAYMENT_FAILED",
                event
        );
    }

    private void save(String eventId, String aggregateId, String eventType, Object event){
        try{
            String payload = objectMapper.writeValueAsString(event);
            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .eventId(eventId)
                    .aggregateType("PAYMENT")
                    .aggregateId(aggregateId)
                    .eventType(eventType)
                    .payload(payload)
                    .status(OutboxStatus.PENDING)
                    .retryCount(0)
                    .build();
            outboxRepository.save(outboxEvent);
        }catch (JsonProcessingException e){
            throw new IllegalStateException("Failed to serialize payment event",e);
        }
    }
}
