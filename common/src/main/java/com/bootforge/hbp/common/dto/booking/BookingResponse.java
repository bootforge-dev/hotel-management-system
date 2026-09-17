package com.bootforge.hbp.common.dto.booking;


import java.math.BigDecimal;
import java.time.LocalDate;

public record BookingResponse(

        Long id,

        String bookingReference,

        Long userId,

        Long hotelId,

        Long roomId,

        LocalDate checkIn,

        LocalDate checkOut,

        BigDecimal totalAmount,

        BookingStatus status

) {
}