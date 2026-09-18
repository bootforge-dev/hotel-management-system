package com.bootforge.hbp.paymentservice.service;

import com.bootforge.hbp.common.dto.payment.PaymentStatus;
import com.bootforge.hbp.common.event.BookingCreatedEvent;
import com.bootforge.hbp.common.event.PaymentFailedEvent;
import com.bootforge.hbp.common.event.PaymentSuccessEvent;
import com.bootforge.hbp.paymentservice.entity.Payment;
import com.bootforge.hbp.paymentservice.entity.ProcessedEvent;
import com.bootforge.hbp.paymentservice.repository.PaymentRepository;
import com.bootforge.hbp.paymentservice.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final OutboxService outboxService;

    @Transactional
    public void processPayment(BookingCreatedEvent event) {

        /*
         * Event-level idempotency
         */
        if (processedEventRepository.existsByEventId(event.eventId())) {
            log.info("Event already processed. eventId={}", event.eventId());
            return;
        }

        /*
         * Business-level idempotency
         */
        var existing = paymentRepository.findByBookingId(event.bookingId());

        if (existing.isPresent()) {
            Payment payment = existing.get();
            if (payment.getStatus() == PaymentStatus.SUCCESS) {
                return;
            }
            if (payment.getStatus() == PaymentStatus.FAILED) {
                return;
            }
        }

        Payment payment = Payment.builder()
                .bookingId(event.bookingId())
                .paymentReference(generatePaymentReference())
                .amount(event.amount())
                .status(PaymentStatus.PENDING)
                .build();

        paymentRepository.save(payment);

        boolean success = paymentProcessGateway(event.amount());
        if (success) {
            payment.setStatus(PaymentStatus.SUCCESS);
            paymentRepository.save(payment);
            PaymentSuccessEvent successEvent =
                    new PaymentSuccessEvent(
                            UUID.randomUUID().toString(),
                            event.bookingId(),
                            event.bookingReference(),
                            payment.getId(),
                            payment.getPaymentReference()
                    );
            outboxService.savePaymentSuccessEvent(successEvent);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            PaymentFailedEvent failedEvent =
                    new PaymentFailedEvent(
                            UUID.randomUUID().toString(),
                            event.bookingId(),
                            event.bookingReference(),
                            "Payment processing failed"
                    );
            outboxService.savePaymentFailedEvent(failedEvent);
        }

        /*
         * Same local DB transaction:
         *
         * payments
         * processed_events
         * outbox_events
         */
        processedEventRepository.save(
                ProcessedEvent.builder()
                        .eventId(event.eventId())
                        .eventType("BOOKING_CREATED")
                        .build()
        );

    }

    private String generatePaymentReference() {
        return "PAY-" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();
    }

    private boolean paymentProcessGateway(BigDecimal amount) {
        return true;
    }


}
