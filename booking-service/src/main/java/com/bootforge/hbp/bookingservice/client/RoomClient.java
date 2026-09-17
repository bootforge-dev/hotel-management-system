package com.bootforge.hbp.bookingservice.client;

import com.bootforge.hbp.common.dto.reservation.ReservationResponse;
import com.bootforge.hbp.common.dto.reservation.ReserveRoomRequest;
import com.bootforge.hbp.common.dto.reservation.RoomAvailabilityResponse;
import com.bootforge.hbp.common.dto.room.RoomResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@FeignClient(
        name = "room-service"
)
public interface RoomClient {

    @GetMapping("/api/v1/rooms/{id}")
    RoomResponse getRoom(@PathVariable Long id);

    @GetMapping("/api/v1/rooms/{roomId}/availability")
    RoomAvailabilityResponse checkAvailability(
            @PathVariable Long roomId,
            @RequestParam LocalDate checkIn,
            @RequestParam LocalDate checkOut);

    @PostMapping("/api/v1/rooms/{roomId}/reserve")
    ReservationResponse reserveRoom(
            @PathVariable Long roomId,
            @Valid @RequestBody ReserveRoomRequest request);

    @PostMapping("/api/v1/rooms/{roomId}/release")
    void releaseRoom(
            @PathVariable Long roomId,
            @RequestParam Long bookingId);

}
