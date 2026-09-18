package com.bootforge.hbp.bookingservice.resilience.exception;

public class RoomServiceUnavailableException extends RuntimeException {

    public RoomServiceUnavailableException(String message) {
        super(message);
    }
}