package com.bootforge.hbp.bookingservice.service;

import com.bootforge.hbp.bookingservice.dto.BookingResponse;
import com.bootforge.hbp.bookingservice.dto.CreateBookingRequest;
import com.bootforge.hbp.bookingservice.entity.Booking;
import com.bootforge.hbp.bookingservice.entity.BookingStatus;
import com.bootforge.hbp.bookingservice.exception.ResourceNotFoundException;
import com.bootforge.hbp.bookingservice.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingResponse createBooking(CreateBookingRequest request) {
        validateDates(request.checkIn(), request.checkOut());

        long nights = ChronoUnit.DAYS.between(request.checkIn(), request.checkOut());

        BigDecimal roomPrice = BigDecimal.valueOf(100);
        BigDecimal totalAmount = roomPrice.multiply(BigDecimal.valueOf(nights));

        Booking booking = Booking.builder()
                .bookingReference(generateBookingReference())
                .userId(request.userId())
                .hotelId(request.hotelId())
                .roomId(request.roomId())
                .checkIn(request.checkIn())
                .checkOut(request.checkOut())
                .totalAmount(totalAmount)
                .status(BookingStatus.PENDING)
                .build();
        Booking savedBooking = bookingRepository.save(booking);
        return toResponse(savedBooking);
    }

    @Transactional(readOnly = true)
    public BookingResponse getBooking(String bookingReference) {
        return toResponse(getBookingByReference(bookingReference));
    }

    public void cancelBooking(String bookingReference) {
        Booking booking = getBookingByReference(bookingReference);
        booking.setStatus(BookingStatus.CANCELLED);
    }

    private Booking getBookingByReference(String bookingReference) {
        return bookingRepository.findByBookingReference(bookingReference).orElseThrow(
                () -> new ResourceNotFoundException("Booking not found")
        );
    }

    private void validateDates(LocalDate checkIn, LocalDate checkOut) {
        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Check-out must be after check-in");
        }
    }

    private String generateBookingReference() {
        return "HTL-" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();
    }

    private BookingResponse toResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getBookingReference(),
                booking.getUserId(),
                booking.getHotelId(),
                booking.getRoomId(),
                booking.getCheckIn(),
                booking.getCheckOut(),
                booking.getTotalAmount(),
                booking.getStatus()
        );
    }
}
