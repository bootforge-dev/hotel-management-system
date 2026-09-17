package com.bootforge.hbp.roomservice.service;

import com.bootforge.hbp.common.dto.reservation.ReservationResponse;
import com.bootforge.hbp.common.dto.reservation.ReservationStatus;
import com.bootforge.hbp.common.dto.reservation.ReserveRoomRequest;
import com.bootforge.hbp.common.dto.reservation.RoomAvailabilityResponse;
import com.bootforge.hbp.common.dto.room.CreateRoomRequest;
import com.bootforge.hbp.common.dto.room.RoomResponse;
import com.bootforge.hbp.roomservice.entity.Room;
import com.bootforge.hbp.roomservice.entity.RoomReservation;
import com.bootforge.hbp.roomservice.exception.RoomNotAvailableException;
import com.bootforge.hbp.roomservice.exception.RoomNotFoundException;
import com.bootforge.hbp.roomservice.repository.RoomRepository;
import com.bootforge.hbp.roomservice.repository.RoomReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomReservationRepository roomReservationRepository;

    public RoomResponse createRoom(CreateRoomRequest request) {
        Room room = Room.builder()
                .hotelId(request.hotelId())
                .roomNumber(request.roomNumber())
                .roomType(request.roomType())
                .pricePerNight(request.pricePerNight())
                .capacity(request.capacity())
                .active(true)
                .build();
        Room savedRoom = roomRepository.save(room);
        return toRoomResponse(savedRoom);
    }

    @Transactional(readOnly = true)
    public RoomResponse getRoom(Long id) {
        return toRoomResponse(getRoomById(id));
    }

    @Transactional(readOnly = true)
    public List<RoomResponse> getRoomsByHotel(Long hotelId) {
        return roomRepository.findByHotelIdAndActiveTrue(hotelId).stream()
                .map(this::toRoomResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RoomAvailabilityResponse checkAvailability(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        validateDates(checkIn, checkOut);
        Room room = getRoomById(roomId);

        if (!Boolean.TRUE.equals(room.getActive())) {
            return new RoomAvailabilityResponse(roomId, false, "Room is inactive");
        }

        boolean reserved = roomReservationRepository.existsByRoomIdAndStatusAndCheckInLessThanAndCheckOutGreaterThan(
                roomId, ReservationStatus.RESERVED, checkOut, checkIn);

        if (reserved) {
            return new RoomAvailabilityResponse(roomId, false, "Room already reserved");
        }

        return new RoomAvailabilityResponse(roomId, true, "Room is available");
    }

    public ReservationResponse reserveRoom(Long roomId, ReserveRoomRequest request) {
        validateDates(request.checkIn(), request.checkOut());
        Room room = getRoomById(roomId);

        if (!Boolean.TRUE.equals(room.getActive())) {
            throw new RoomNotAvailableException(roomId);
        }

        var existingReservation = roomReservationRepository.findByBookingId(request.bookingId());
        if (existingReservation.isPresent()) {
            return toReservationResponse(existingReservation.get());
        }

        boolean alreadyReserved = roomReservationRepository.
                existsByRoomIdAndStatusAndCheckInLessThanAndCheckOutGreaterThan(roomId, ReservationStatus.RESERVED, request.checkOut(), request.checkIn());

        if (alreadyReserved) {
            throw new RoomNotAvailableException(roomId);
        }

        RoomReservation reservation = RoomReservation.builder()
                .roomId(roomId)
                .bookingId(request.bookingId())
                .checkIn(request.checkIn())
                .checkOut(request.checkOut())
                .status(ReservationStatus.RESERVED)
                .build();
        RoomReservation savedReservation = roomReservationRepository.save(reservation);
        return toReservationResponse(savedReservation);
    }

    public void releaseRoom(Long roomId, Long bookingId) {
        RoomReservation reservation = roomReservationRepository.findByBookingId(bookingId).orElseThrow(
                () -> new RuntimeException("Reservation not found for booking: " + bookingId)
        );

        if (!reservation.getRoomId().equals(roomId)) {
            throw new RuntimeException("Reservation does not belong to room");
        }

        reservation.setStatus(ReservationStatus.RELEASED);
    }

    public void deactivateRoom(Long id) {
        Room room = getRoomById(id);
        room.setActive(false);
    }


    private void validateDates(LocalDate checkIn, LocalDate checkOut) {

        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Check-in and check-out are required");
        }

        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Check-out must be after check-in");
        }
    }

    private RoomResponse toRoomResponse(Room room) {
        return RoomResponse.builder()
                .id(room.getId())
                .hotelId(room.getHotelId())
                .roomNumber(room.getRoomNumber())
                .roomType(room.getRoomType())
                .pricePerNight(room.getPricePerNight())
                .capacity(room.getCapacity())
                .active(room.getActive())
                .build();
    }

    private ReservationResponse toReservationResponse(RoomReservation reservation) {
        return ReservationResponse.builder()
                .reservationId(reservation.getId())
                .roomId(reservation.getRoomId())
                .bookingId(reservation.getBookingId())
                .checkIn(reservation.getCheckIn())
                .checkOut(reservation.getCheckOut())
                .status(reservation.getStatus())
                .build();
    }


    private Room getRoomById(Long id) {
        return roomRepository.findById(id).orElseThrow(
                () -> new RoomNotFoundException(id)
        );
    }
}
