package com.bootforge.hbp.hotelservice.service;

import com.bootforge.hbp.common.dto.hotel.CreateHotelRequest;
import com.bootforge.hbp.common.dto.hotel.HotelResponse;
import com.bootforge.hbp.hotelservice.entity.Hotel;
import com.bootforge.hbp.hotelservice.exception.ResourceNotFoundException;
import com.bootforge.hbp.hotelservice.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class HotelService {

    private final HotelRepository hotelRepository;

    public HotelResponse create(CreateHotelRequest request) {
        Hotel hotel = Hotel.builder()
                .name(request.name())
                .description(request.description())
                .address(request.address())
                .city(request.city())
                .country(request.country())
                .rating(0.0)
                .active(true)
                .build();
        Hotel savedHotel = hotelRepository.save(hotel);
        return toResponse(savedHotel);
    }

    @Transactional(readOnly = true)
    public HotelResponse getHotel(Long id) {
        return toResponse(getById(id));
    }

    @Transactional(readOnly = true)
    public List<HotelResponse> getAllHotels() {
        return hotelRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<HotelResponse> searchByCity(String city) {
        return hotelRepository.findByCityIgnoreCaseAndActiveTrue(city).stream()
                .map(this::toResponse)
                .toList();
    }

    public void deleteHotel(Long id) {
        Hotel hotel = getById(id);
        hotel.setActive(false);

    }

    public Hotel getById(Long id) {
        return hotelRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Hotel not found by this id: " + id)
        );
    }

    private HotelResponse toResponse(Hotel hotel) {
        return HotelResponse.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .description(hotel.getDescription())
                .address(hotel.getAddress())
                .city(hotel.getCity())
                .country(hotel.getCountry())
                .rating(hotel.getRating())
                .active(hotel.getActive())
                .build();
    }
}
