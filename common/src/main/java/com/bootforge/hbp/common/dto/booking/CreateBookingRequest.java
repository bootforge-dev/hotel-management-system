package com.bootforge.hbp.common.dto.booking;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateBookingRequest(

        @NotNull(message = "User ID is required")
        Long userId,

        @NotNull(message = "Hotel ID is required")
        Long hotelId,

        @NotNull(message = "Room ID is required")
        Long roomId,

        @NotNull(message = "Check-in date is required")
        LocalDate checkIn,

        @NotNull(message = "Check-out date is required")
        LocalDate checkOut

) {
}