package com.bootforge.hbp.common.dto.hotel;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CreateHotelRequest(
        @NotBlank(message = "Name must be required")
        String name,

        @NotBlank(message = "Description must be required")
        String description,

        @NotBlank(message = "Address must be required")
        String address,

        @NotBlank(message = "City must be required")
        String city,

        @NotBlank(message = "Country must be required")
        String country
) {
}
