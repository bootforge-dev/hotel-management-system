package com.bootforge.hbp.bookingservice.client;

import com.bootforge.hbp.common.dto.hotel.HotelResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "hotel-service"
)
public interface HotelClient {

    @GetMapping("/api/v1/hotels/{id}")
    HotelResponse getHotel(@PathVariable Long id);
}
