package com.bootforge.hbp.roomservice.controller;

import com.bootforge.hbp.common.dto.reservation.ReservationResponse;
import com.bootforge.hbp.common.dto.reservation.ReserveRoomRequest;
import com.bootforge.hbp.common.dto.reservation.RoomAvailabilityResponse;
import com.bootforge.hbp.common.dto.room.CreateRoomRequest;
import com.bootforge.hbp.common.dto.room.RoomResponse;
import com.bootforge.hbp.roomservice.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    // CREATE ROOM
    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(@Valid @RequestBody CreateRoomRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(roomService.createRoom(request));
    }

    // GET ROOM
    @GetMapping("/{id}")
    public ResponseEntity<RoomResponse> getRoom(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoom(id));
    }

    // GET ROOMS BY HOTEL
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<RoomResponse>> getRoomsByHotel(@PathVariable Long hotelId) {
        return ResponseEntity.ok(roomService.getRoomsByHotel(hotelId));
    }

    // DEACTIVATE ROOM
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateRoom(@PathVariable Long id) {
        roomService.deactivateRoom(id);
        return ResponseEntity.accepted().build();
    }

    // CHECK AVAILABILITY
    @GetMapping("/{roomId}/availability")
    public RoomAvailabilityResponse checkAvailability(
            @PathVariable Long roomId,
            @RequestParam LocalDate checkIn,
            @RequestParam LocalDate checkOut) {

        return roomService.checkAvailability(roomId, checkIn, checkOut);
    }

    // RESERVE ROOM
    @PostMapping("/{roomId}/reserve")
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse reserveRoom(
            @PathVariable Long roomId, @Valid @RequestBody ReserveRoomRequest request) {
        return roomService.reserveRoom(roomId, request);
    }


    // RELEASE ROOM
    @PostMapping("/{roomId}/release")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void releaseRoom(@PathVariable Long roomId, @RequestParam Long bookingId) {
        roomService.releaseRoom(roomId, bookingId);
    }

}
