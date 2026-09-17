package com.bootforge.hbp.common.event;

public record PaymentFailedEvent(

        String eventId,

        Long bookingId,

        String bookingReference,

        String reason

) {
}