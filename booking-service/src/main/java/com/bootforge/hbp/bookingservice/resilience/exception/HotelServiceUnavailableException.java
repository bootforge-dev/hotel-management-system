package com.bootforge.hbp.bookingservice.resilience.exception;

public class HotelServiceUnavailableException extends RuntimeException {

    public HotelServiceUnavailableException(String message) {
        super(message);
    }
}