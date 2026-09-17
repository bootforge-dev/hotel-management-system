package com.bootforge.hbp.common.dto.hotel;


import lombok.Builder;

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
