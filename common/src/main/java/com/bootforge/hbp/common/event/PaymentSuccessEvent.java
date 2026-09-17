package com.bootforge.hbp.common.event;

public record PaymentSuccessEvent(

        String eventId,

        Long bookingId,

        String bookingReference,

        Long paymentId,

        String paymentReference

) {
}