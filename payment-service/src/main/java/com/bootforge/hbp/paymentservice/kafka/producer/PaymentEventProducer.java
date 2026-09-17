package com.bootforge.hbp.paymentservice.kafka.producer;

import com.bootforge.hbp.common.event.BookingCreatedEvent;
import com.bootforge.hbp.common.event.PaymentFailedEvent;
import com.bootforge.hbp.common.event.PaymentSuccessEvent;
import com.bootforge.hbp.paymentservice.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;


    public void publishPaymentSuccess(BookingCreatedEvent event, Payment payment) {
        PaymentSuccessEvent successEvent = new PaymentSuccessEvent(
                UUID.randomUUID().toString(),
                event.bookingId(),
                event.bookingReference(),
                payment.getId(),
                payment.getPaymentReference()
        );
        kafkaTemplate.send(
                "payment-success",
                event.bookingReference(),
                successEvent
        );
    }


    public void publishPaymentFailed(BookingCreatedEvent event, String reason) {
        PaymentFailedEvent failedEvent = new PaymentFailedEvent(
                UUID.randomUUID().toString(),
                event.bookingId(),
                event.bookingReference(),
                reason
        );
        kafkaTemplate.send(
                "payment-failed",
                event.bookingReference(),
                failedEvent
        );
    }
}
