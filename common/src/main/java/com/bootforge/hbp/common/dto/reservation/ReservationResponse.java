package com.bootforge.hbp.common.dto.reservation;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record ReservationResponse(

        Long reservationId,

        Long roomId,

        Long bookingId,

        LocalDate checkIn,

        LocalDate checkOut,

        ReservationStatus status

) {
}