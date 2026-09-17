package com.bootforge.hbp.paymentservice.kafka.consumer;

import com.bootforge.hbp.common.event.BookingCreatedEvent;
import com.bootforge.hbp.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingCreatedConsumer {

    private final PaymentService paymentService;

    @KafkaListener(
            topics = "booking-created",
            groupId = "payment-service"
    )
    public void consume(BookingCreatedEvent event){
        paymentService.processPayment(event);
    }

}
