package com.bootforge.hbp.roomservice.repository;

import com.bootforge.hbp.common.dto.reservation.ReservationStatus;
import com.bootforge.hbp.roomservice.entity.RoomReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoomReservationRepository extends JpaRepository<RoomReservation, Long> {
    List<RoomReservation> findByRoomIdAndStatus(Long roomId, ReservationStatus status);

    Optional<RoomReservation> findByBookingId(Long bookingId);

    boolean existsByRoomIdAndStatusAndCheckInLessThanAndCheckOutGreaterThan(Long roomId, ReservationStatus status, LocalDate checkOut, LocalDate checkIn
    );
}
