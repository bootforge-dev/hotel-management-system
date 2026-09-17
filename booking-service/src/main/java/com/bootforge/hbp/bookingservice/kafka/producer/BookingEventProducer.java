package com.bootforge.hbp.bookingservice.kafka.producer;

import com.bootforge.hbp.common.event.BookingCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingEventProducer {

    private static final String BOOKING_CREATED = "booking-created";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishBookingCreated(BookingCreatedEvent event) {
        kafkaTemplate.send(
                BOOKING_CREATED,
                event.bookingReference(),
                event
        );
    }

}
