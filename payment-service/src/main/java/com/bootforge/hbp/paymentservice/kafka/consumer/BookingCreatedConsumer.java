package com.bootforge.hbp.paymentservice.kafka.consumer;

import com.bootforge.hbp.common.event.BookingCreatedEvent;
import com.bootforge.hbp.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingCreatedConsumer {

    private final PaymentService paymentService;

    @RetryableTopic(
            attempts = "4",
            backOff = @BackOff(
                    delay = 2000,
                    multiplier = 2.0
            ),
            dltTopicSuffix = ".DLT"
    )
    @KafkaListener(
            topics = "booking-created",
            groupId = "payment-service"
    )
    public void consume(BookingCreatedEvent event){
        log.info(
                "Received booking-created event. " + "eventId={}, bookingId={}, bookingReference={}",
                event.eventId(), event.bookingId(), event.bookingReference()
        );

        paymentService.processPayment(event);

        /*
        * try to run payment fail , then reties will happen and event will goes to DLT
         */
//        throw new RuntimeException(
//                "Testing payment retry"
//        );
    }

    @KafkaListener(
            topics = "booking-created.DLT",
            groupId = "payment-service-dlt"
    )
    public void consumeDlt(String payload){
        log.error("Booking event moved to DLT. payload={}",payload);
    }

}
