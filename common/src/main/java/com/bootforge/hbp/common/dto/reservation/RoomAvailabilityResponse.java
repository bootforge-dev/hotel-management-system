package com.bootforge.hbp.common.dto.reservation;

public record RoomAvailabilityResponse(

        Long roomId,

        boolean available,

        String message

) {
}