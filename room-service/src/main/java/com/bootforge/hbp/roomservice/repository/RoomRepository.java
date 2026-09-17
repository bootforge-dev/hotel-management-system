package com.bootforge.hbp.roomservice.repository;

import com.bootforge.hbp.roomservice.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByHotelIdAndActiveTrue(Long hotelId);
}
