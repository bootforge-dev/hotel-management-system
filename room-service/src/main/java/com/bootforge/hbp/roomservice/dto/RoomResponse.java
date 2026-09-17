package com.bootforge.hbp.roomservice.dto;

import com.bootforge.hbp.roomservice.entity.RoomType;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record RoomResponse(
        Long id,
        Long hotelId,
        String roomNumber,
        RoomType roomType,
        BigDecimal pricePerNight,
        Integer capacity,
        Boolean active
) {
}
