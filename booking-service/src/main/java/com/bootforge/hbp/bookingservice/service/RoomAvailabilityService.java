package com.bootforge.hbp.bookingservice.service;

import com.bootforge.hbp.bookingservice.client.RoomClient;
import com.bootforge.hbp.bookingservice.redis.AvailabilityCacheService;
import com.bootforge.hbp.common.dto.reservation.RoomAvailabilityResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RoomAvailabilityService {

    private final RoomClient roomClient;
    private final AvailabilityCacheService availabilityCacheService;

    public RoomAvailabilityResponse checkAvailability(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        Boolean cached = availabilityCacheService.get(roomId, checkIn, checkOut);
        if (cached != null) {
            return new RoomAvailabilityResponse(roomId, cached, cached
                    ? "Room available"
                    : "Room not available");
        }
        RoomAvailabilityResponse response = roomClient.checkAvailability(roomId, checkIn, checkOut);
        availabilityCacheService.put(roomId, checkIn, checkOut, response.available());

        return response;
    }

}
