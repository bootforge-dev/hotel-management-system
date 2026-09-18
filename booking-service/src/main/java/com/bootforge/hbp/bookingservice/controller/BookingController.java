package com.bootforge.hbp.bookingservice.controller;

import com.bootforge.hbp.bookingservice.service.BookingService;
import com.bootforge.hbp.common.dto.booking.BookingResponse;
import com.bootforge.hbp.common.dto.booking.CreateBookingRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createBooking(
            @RequestHeader("Idempotency-Key")
            String idempotencyKey,
            @Valid @RequestBody CreateBookingRequest request) {

        return bookingService.createBooking(request, idempotencyKey);
    }

    @GetMapping("/{bookingReference}")
    public ResponseEntity<BookingResponse> getBooking(
            @PathVariable String bookingReference) {

        return ResponseEntity.ok(bookingService.getBooking(bookingReference));
    }

    @PostMapping("/{bookingReference}/cancel")
    public ResponseEntity<Void> cancelBooking(
            @PathVariable String bookingReference) {

        bookingService.cancelBooking(bookingReference);
        return ResponseEntity.accepted().build();
    }

}
