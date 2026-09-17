package com.bootforge.hbp.bookingservice.dto;

import com.bootforge.hbp.bookingservice.entity.BookingStatus;

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