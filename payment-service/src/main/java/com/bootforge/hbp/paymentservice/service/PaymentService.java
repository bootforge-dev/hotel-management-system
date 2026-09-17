package com.bootforge.hbp.paymentservice.service;

import com.bootforge.hbp.common.dto.payment.PaymentStatus;
import com.bootforge.hbp.common.event.BookingCreatedEvent;
import com.bootforge.hbp.paymentservice.entity.Payment;
import com.bootforge.hbp.paymentservice.kafka.producer.PaymentEventProducer;
import com.bootforge.hbp.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentEventProducer paymentEventProducer;


    @Transactional
    public void processPayment(BookingCreatedEvent event) {

        // ==========================================
        // IDEMPOTENCY CHECK
        // ==========================================
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

        // ==========================================
        // CREATE PAYMENT
        // ==========================================
        Payment payment = Payment.builder()
                .bookingId(event.bookingId())
                .paymentReference(generatePaymentReference())
                .amount(event.amount())
                .status(PaymentStatus.PENDING)
                .build();

        paymentRepository.save(payment);

        // ==========================================
        // SIMULATE PAYMENT
        // ==========================================
        boolean success = paymentProcessGateway(event.amount());

        if(success){
            payment.setStatus(PaymentStatus.SUCCESS);
            paymentRepository.save(payment);
            paymentEventProducer.publishPaymentSuccess(event, payment);
        }else{
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            paymentEventProducer.publishPaymentFailed(event, "Payment processing failed");
        }
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
