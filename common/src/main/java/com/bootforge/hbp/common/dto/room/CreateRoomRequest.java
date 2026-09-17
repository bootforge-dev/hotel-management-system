package com.bootforge.hbp.common.dto.room;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CreateRoomRequest(
        @NotNull(message = "Hotel ID is required")
        Long hotelId,

        @NotBlank(message = "Room Number is required")
        String roomNumber,

        @NotNull(message = "Room Type is required")
        RoomType roomType,

        @NotNull(message = "Room price is required")
        @Positive(message = "Price not be negative")
        BigDecimal pricePerNight,

        @NotNull(message = "Capacity is required")
        @Positive(message = "Capacity not be negative")
        Integer capacity
) {
}
