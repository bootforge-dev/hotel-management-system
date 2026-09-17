package com.bootforge.hbp.common.dto.room;

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
