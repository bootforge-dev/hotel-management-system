package com.bootforge.hbp.bookingservice.kafka.consumer;

import com.bootforge.hbp.bookingservice.client.RoomClient;
import com.bootforge.hbp.bookingservice.entity.Booking;
import com.bootforge.hbp.bookingservice.exception.ResourceNotFoundException;
import com.bootforge.hbp.bookingservice.repository.BookingRepository;
import com.bootforge.hbp.common.dto.booking.BookingStatus;
import com.bootforge.hbp.common.event.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PaymentFailedConsumer {
    private final BookingRepository bookingRepository;
    private final RoomClient roomClient;

    @KafkaListener(
            topics = "payment-failed",
            groupId = "booking-service"
    )
    @Transactional
    public void consume(PaymentFailedEvent event) {
        Booking booking = bookingRepository.findById(event.bookingId()).orElseThrow(
                () -> new ResourceNotFoundException("Booking not found: " + event.bookingId())
        );
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return;
        }

        booking.setStatus(BookingStatus.PAYMENT_FAILED);
        bookingRepository.save(booking);

        roomClient.releaseRoom(booking.getRoomId(), booking.getId());

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }
}
