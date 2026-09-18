package com.bootforge.hbp.bookingservice.service;

import com.bootforge.hbp.bookingservice.entity.OutboxEvent;
import com.bootforge.hbp.bookingservice.repository.OutboxEventRepository;
import com.bootforge.hbp.common.dto.booking.outbox.OutboxStatus;
import com.bootforge.hbp.common.event.BookingCreatedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public void saveBookingCreatedEvent(BookingCreatedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .eventId(event.eventId())
                    .aggregateType("BOOKING")
                    .aggregateId(String.valueOf(event.bookingId()))
                    .eventType("BOOKING_CREATED")
                    .payload(payload)
                    .status(OutboxStatus.PENDING)
                    .retryCount(10)
                    .build();
            outboxEventRepository.save(outboxEvent);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize BookingCreatedEvent", e);
        }
    }

}
