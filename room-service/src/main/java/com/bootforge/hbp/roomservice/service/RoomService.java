package com.bootforge.hbp.roomservice.service;

import com.bootforge.hbp.roomservice.dto.CreateRoomRequest;
import com.bootforge.hbp.roomservice.dto.RoomResponse;
import com.bootforge.hbp.roomservice.entity.Room;
import com.bootforge.hbp.roomservice.exception.ResourceNotFoundException;
import com.bootforge.hbp.roomservice.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomResponse createRoom(CreateRoomRequest request) {
        Room room = Room.builder()
                .hotelId(request.hotelId())
                .roomNumber(request.roomNumber())
                .roomType(request.roomType())
                .pricePerNight(request.pricePerNight())
                .capacity(request.capacity())
                .active(true)
                .build();
        Room savedRoom = roomRepository.save(room);
        return toResponse(savedRoom);
    }

    @Transactional(readOnly = true)
    public RoomResponse getRoom(Long id) {
        return toResponse(getRoomById(id));
    }

    @Transactional(readOnly = true)
    public List<RoomResponse> getRoomsByHotel(Long hotelId) {
        return roomRepository.findByHotelIdAndActiveTrue(hotelId).stream()
                .map(this::toResponse)
                .toList();
    }

    public void deleteHotel(Long id) {
        roomRepository.delete(getRoomById(id));
    }

    private RoomResponse toResponse(Room room) {
        return RoomResponse.builder()
                .id(room.getId())
                .hotelId(room.getHotelId())
                .roomNumber(room.getRoomNumber())
                .roomType(room.getRoomType())
                .pricePerNight(room.getPricePerNight())
                .capacity(room.getCapacity())
                .active(room.getActive())
                .build();
    }

    private Room getRoomById(Long id) {
        return roomRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Room not found with ID: " + id)
        );
    }
}
