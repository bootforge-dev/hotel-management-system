package com.bootforge.hbp.bookingservice.kafka.consumer;

import com.bootforge.hbp.bookingservice.entity.Booking;
import com.bootforge.hbp.bookingservice.exception.ResourceNotFoundException;
import com.bootforge.hbp.bookingservice.repository.BookingRepository;
import com.bootforge.hbp.common.dto.booking.BookingStatus;
import com.bootforge.hbp.common.event.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PaymentSuccessConsumer {
    private final BookingRepository bookingRepository;

    @KafkaListener(
            topics = "payment-success",
            groupId = "booking-service"
    )
    @Transactional
    public void consume(PaymentSuccessEvent event) {
        Booking booking = bookingRepository.findById(event.bookingId()).orElseThrow(
                () -> new ResourceNotFoundException("Booking not found: " + event.bookingId())
        );
        if (booking.getStatus() == BookingStatus.CONFIRMED)
            return;

        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
    }
}
