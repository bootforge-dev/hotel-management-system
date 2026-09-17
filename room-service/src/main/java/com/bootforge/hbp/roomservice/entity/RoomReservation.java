package com.bootforge.hbp.roomservice.entity;

import com.bootforge.hbp.common.dto.reservation.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "room_reservations",
        indexes = {
                @Index(
                        name = "idx_room_dates",
                        columnList = "room_id,check_in,check_out"
                )
        }
)
public class RoomReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(nullable = false, name = "booking_id", unique = true)
    private Long bookingId;

    @Column(nullable = false, name = "check_in")
    private LocalDate checkIn;

    @Column(nullable = false, name = "check_out")
    private LocalDate checkOut;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = ReservationStatus.RESERVED;
        }
    }
}
