package com.bootforge.hbp.roomservice.exception;

public class RoomNotAvailableException extends RuntimeException {
    public RoomNotAvailableException(Long roomId) {
        super("Room is not available for the selected dates: " + roomId);
    }
}