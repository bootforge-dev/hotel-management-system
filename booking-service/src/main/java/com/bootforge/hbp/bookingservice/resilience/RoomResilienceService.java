package com.bootforge.hbp.bookingservice.resilience;

import com.bootforge.hbp.bookingservice.client.RoomClient;
import com.bootforge.hbp.bookingservice.resilience.exception.RoomServiceUnavailableException;
import com.bootforge.hbp.common.dto.reservation.ReservationResponse;
import com.bootforge.hbp.common.dto.reservation.ReserveRoomRequest;
import com.bootforge.hbp.common.dto.reservation.RoomAvailabilityResponse;
import com.bootforge.hbp.common.dto.room.RoomResponse;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RoomResilienceService {
    private final RoomClient roomClient;

    @CircuitBreaker(name = "roomService", fallbackMethod = "getRoomFallback")
    @Retry(name = "roomService")
    @Bulkhead(name = "roomService", type = Bulkhead.Type.SEMAPHORE)
    public RoomResponse getRoom(Long roomId) {
        return roomClient.getRoom(roomId);
    }

    @CircuitBreaker(name = "roomService", fallbackMethod = "checkAvailabilityFallback")
    @Retry(name = "roomService")
    @Bulkhead(name = "roomService", type = Bulkhead.Type.SEMAPHORE)
    public RoomAvailabilityResponse checkAvailability(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        return roomClient.checkAvailability(roomId, checkIn, checkOut);
    }

    @CircuitBreaker(name = "roomReservationService", fallbackMethod = "reserveRoomFallback")
    @Bulkhead(name = "roomReservationService", type = Bulkhead.Type.SEMAPHORE)
    public ReservationResponse reserveRoom(Long roomId, ReserveRoomRequest request) {

        /*
         * IMPORTANT:
         *
         * Do NOT retry reserveRoom automatically.
         *
         * Reservation is a state-changing operation.
         * A timeout may happen after Room Service
         * successfully creates the reservation.
         *
         * Retrying could create duplicate reservations
         * unless the Room Service API is idempotent.
         */

        return roomClient.reserveRoom(roomId, request);
    }

    @CircuitBreaker(name = "roomReservationService", fallbackMethod = "releaseRoomFallback")
    @Bulkhead(name = "roomReservationService", type = Bulkhead.Type.SEMAPHORE)
    public void releaseRoom(Long roomId, Long bookingId) {
        roomClient.releaseRoom(roomId, bookingId);
    }

    private RoomResponse getRoomFallback(Long roomId, Throwable throwable) {
        throw new RoomServiceUnavailableException(
                "Room service is currently unavailable. Please try again later."
        );
    }

    private RoomAvailabilityResponse checkAvailabilityFallback(Long roomId, LocalDate checkIn, LocalDate checkOut, Throwable throwable
    ) {
        throw new RoomServiceUnavailableException(
                "Room availability service is currently unavailable. Please try again later."
        );
    }

    private ReservationResponse reserveRoomFallback(Long roomId, ReserveRoomRequest request, Throwable throwable
    ) {
        throw new RoomServiceUnavailableException(
                "Unable to reserve room because Room Service is unavailable."
        );
    }

    private void releaseRoomFallback(Long roomId, Long bookingId, Throwable throwable
    ) {

        /*
         * Release failure should not be silently ignored.
         *
         * In a later phase this should be handled using
         * an asynchronous compensation/outbox mechanism.
         */
        throw new RoomServiceUnavailableException(
                "Unable to release room because Room Service is unavailable."
        );
    }

}
