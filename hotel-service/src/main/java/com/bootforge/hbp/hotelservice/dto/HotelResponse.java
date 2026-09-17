package com.bootforge.hbp.hotelservice.dto;


import jakarta.persistence.Column;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record HotelResponse(
        Long id,
        String name,
        String description,
        String address,
        String city,
        String country,
        Double rating,
        Boolean active
) {
}
