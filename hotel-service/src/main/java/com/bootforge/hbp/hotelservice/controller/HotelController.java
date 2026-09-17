package com.bootforge.hbp.hotelservice.controller;


import com.bootforge.hbp.common.dto.hotel.CreateHotelRequest;
import com.bootforge.hbp.common.dto.hotel.HotelResponse;
import com.bootforge.hbp.hotelservice.service.HotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;

    @PostMapping
    public ResponseEntity<HotelResponse> crateHotel(
            @Valid @RequestBody CreateHotelRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(hotelService.create(request));
    }

    @GetMapping("/{id}")
    public HotelResponse getHotel(
            @PathVariable Long id) {

        return hotelService.getHotel(id);
    }

    @GetMapping
    public ResponseEntity<List<HotelResponse>> getAllHotels() {
        return ResponseEntity.ok(hotelService.getAllHotels());
    }

    @GetMapping("/search")
    public ResponseEntity<List<HotelResponse>> searchHotels(@RequestParam String city) {
        return ResponseEntity.ok(hotelService.searchByCity(city));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHotel(@PathVariable Long id) {
        hotelService.deleteHotel(id);
        return ResponseEntity.accepted().build();
    }

}
