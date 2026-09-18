package com.bootforge.hbp.bookingservice.resilience;

import com.bootforge.hbp.bookingservice.client.HotelClient;
import com.bootforge.hbp.bookingservice.resilience.exception.HotelServiceUnavailableException;
import com.bootforge.hbp.common.dto.hotel.HotelResponse;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HotelResilienceService {

    private final HotelClient hotelClient;

    @CircuitBreaker(name = "hotelService", fallbackMethod = "getHotelFallback")
    @Retry(name = "hotelService")
    @Bulkhead(name = "hotelService", type = Bulkhead.Type.SEMAPHORE)
    public HotelResponse getHotel(Long hotelId) {
        return hotelClient.getHotel(hotelId);
    }

    private HotelResponse getHotelFallback(Long hotelId, Throwable throwable) {
        throw new HotelServiceUnavailableException("Hotel service is currently unavailable. Please try again later.");
    }

}
