package com.bootforge.hbp.bookingservice.service;

import com.bootforge.hbp.bookingservice.client.HotelClient;
import com.bootforge.hbp.bookingservice.client.RoomClient;
import com.bootforge.hbp.bookingservice.entity.Booking;
import com.bootforge.hbp.bookingservice.exception.ResourceNotFoundException;
import com.bootforge.hbp.bookingservice.repository.BookingRepository;
import com.bootforge.hbp.common.dto.booking.BookingResponse;
import com.bootforge.hbp.common.dto.booking.BookingStatus;
import com.bootforge.hbp.common.dto.booking.CreateBookingRequest;
import com.bootforge.hbp.common.dto.hotel.HotelResponse;
import com.bootforge.hbp.common.dto.room.RoomResponse;
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
    private final HotelClient hotelClient;
    private final RoomClient roomClient;

    public BookingResponse createBooking(CreateBookingRequest request) {
        validateDates(request.checkIn(), request.checkOut());

        HotelResponse hotel = hotelClient.getHotel(request.hotelId());
        if (hotel == null) {
            throw new ResourceNotFoundException("Hotel not found");
        }
        if (!Boolean.TRUE.equals(hotel.active())) {
            throw new ResourceNotFoundException("Hotel is not active");
        }

        RoomResponse room = roomClient.getRoom(request.roomId());
        if (!room.hotelId().equals(request.hotelId())) {
            throw new ResourceNotFoundException("Room does not belongs to hotel");
        }
        if (!Boolean.TRUE.equals(room.active())) {
            throw new ResourceNotFoundException("Room is not active");
        }

        long nights = ChronoUnit.DAYS.between(request.checkIn(), request.checkOut());

        BigDecimal totalAmount = room.pricePerNight()
                .multiply(
                        BigDecimal.valueOf(nights));

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
