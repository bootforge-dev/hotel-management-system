package com.bootforge.hbp.common.dto.reservation;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ReserveRoomRequest(
        @NotNull
        Long bookingId,

        @NotNull
        LocalDate checkIn,

        @NotNull
        LocalDate checkOut
) {
}
