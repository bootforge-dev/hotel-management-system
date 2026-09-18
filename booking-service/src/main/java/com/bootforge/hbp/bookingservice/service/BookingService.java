package com.bootforge.hbp.bookingservice.service;

import com.bootforge.hbp.bookingservice.client.HotelClient;
import com.bootforge.hbp.bookingservice.client.RoomClient;
import com.bootforge.hbp.bookingservice.entity.Booking;
import com.bootforge.hbp.bookingservice.exception.ResourceNotFoundException;
import com.bootforge.hbp.bookingservice.redis.AvailabilityCacheService;
import com.bootforge.hbp.bookingservice.repository.BookingRepository;
import com.bootforge.hbp.common.dto.booking.BookingResponse;
import com.bootforge.hbp.common.dto.booking.BookingStatus;
import com.bootforge.hbp.common.dto.booking.CreateBookingRequest;
import com.bootforge.hbp.common.dto.hotel.HotelResponse;
import com.bootforge.hbp.common.dto.reservation.ReserveRoomRequest;
import com.bootforge.hbp.common.dto.reservation.RoomAvailabilityResponse;
import com.bootforge.hbp.common.dto.room.RoomResponse;
import com.bootforge.hbp.common.event.BookingCreatedEvent;
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
    private final OutboxService outboxService;

    private final RoomAvailabilityService roomAvailabilityService;
    private final DistributedLockService distributedLockService;
    private final AvailabilityCacheService availabilityCacheService;

    public BookingResponse createBooking(CreateBookingRequest request) {
        validateDates(request.checkIn(), request.checkOut());

        // ==========================================
        // 1. Validate Hotel
        // ==========================================

        HotelResponse hotel = hotelClient.getHotel(request.hotelId());
        if (hotel == null) {
            throw new ResourceNotFoundException("Hotel not found");
        }
        if (!Boolean.TRUE.equals(hotel.active())) {
            throw new ResourceNotFoundException("Hotel is not active");
        }

        // ==========================================
        // 2. Get Room
        // ==========================================

        RoomResponse room = roomClient.getRoom(request.roomId());

        // ==========================================
        // 3. Validate Room belongs to Hotel
        // ==========================================

        if (!room.hotelId().equals(request.hotelId())) {
            throw new ResourceNotFoundException("Room does not belongs to hotel");
        }

        // ==========================================
        // 4. Validate Room Active
        // ==========================================

        if (!Boolean.TRUE.equals(room.active())) {
            throw new ResourceNotFoundException("Room is not active");
        }

        String lockValue = UUID.randomUUID().toString();
        boolean locked = distributedLockService.tryLock(
                request.roomId(),
                request.checkIn(),
                request.checkOut(),
                lockValue
        );
        if (!locked) {
            throw new IllegalStateException("Room is currently being booked by another request");
        }

        // ==========================================
        // 5. Check Availability
        // ==========================================

        try {
            RoomAvailabilityResponse availability =
                    roomClient.checkAvailability(request.roomId(), request.checkIn(), request.checkOut());

            if (!availability.available()) {
                throw new RuntimeException(availability.message());
            }

            // ==========================================
            // 6. Calculate Price
            // ==========================================

            long nights = ChronoUnit.DAYS.between(request.checkIn(), request.checkOut());

            BigDecimal totalAmount = room.pricePerNight()
                    .multiply(
                            BigDecimal.valueOf(nights));

            // ==========================================
            // 7. Create Booking
            // ==========================================

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

            // ==========================================
            // 8. Reserve Room
            // ==========================================
            ReserveRoomRequest reserveRequest = new ReserveRoomRequest(
                    savedBooking.getId(),
                    savedBooking.getCheckIn(),
                    savedBooking.getCheckOut());

            roomClient.reserveRoom(request.roomId(), reserveRequest);

            // ==========================================
            // 9. Change status
            // ==========================================

            savedBooking.setStatus(BookingStatus.PAYMENT_PENDING);

            // ==========================================
            // 10. Send Event to Kafka Topic
            // ==========================================
            BookingCreatedEvent event = new BookingCreatedEvent(
                    UUID.randomUUID().toString(),
                    savedBooking.getId(),
                    savedBooking.getBookingReference(),
                    savedBooking.getUserId(),
                    savedBooking.getHotelId(),
                    savedBooking.getRoomId(),
                    savedBooking.getTotalAmount()
            );

            /*
             * IMPORTANT:
             * Do NOT publish directly to Kafka.
             * Store the event in outbox_events.
             */
            outboxService.saveBookingCreatedEvent(event);

            return toResponse(savedBooking);
        } finally {
            distributedLockService.unlock(
                    request.roomId(),
                    request.checkIn(),
                    request.checkOut(),
                    lockValue
            );
        }
    }

    @Transactional(readOnly = true)
    public BookingResponse getBooking(String bookingReference) {
        return toResponse(getBookingByReference(bookingReference));
    }

    public void cancelBooking(String bookingReference) {
        Booking booking = getBookingByReference(bookingReference);

        roomClient.releaseRoom(booking.getRoomId(), booking.getId());

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
