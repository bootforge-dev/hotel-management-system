package com.bootforge.hbp.hotelservice.repository;

import com.bootforge.hbp.hotelservice.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel, Long> {

    List<Hotel> findByCityIgnoreCaseAndActiveTrue(String city);

}
