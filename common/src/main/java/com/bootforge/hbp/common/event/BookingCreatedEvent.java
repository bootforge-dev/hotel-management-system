package com.bootforge.hbp.common.event;

import java.math.BigDecimal;

public record BookingCreatedEvent(
        String eventId,

        Long bookingId,

        String bookingReference,

        Long userId,

        Long hotelId,

        Long roomId,

        BigDecimal amount
) {
}
