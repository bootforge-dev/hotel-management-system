package com.bootforge.hbp.bookingservice.client;

import com.bootforge.hbp.common.dto.room.RoomResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "room-service",
        url = "${client.room-service.url}"
)
public interface RoomClient {

    @GetMapping("/api/v1/rooms/{id}")
    RoomResponse getRoom(@PathVariable Long id);

}
