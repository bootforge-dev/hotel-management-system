package com.bootforge.hbp.bookingservice.repository;

import com.bootforge.hbp.bookingservice.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByBookingReference(
            String bookingReference
    );
}
