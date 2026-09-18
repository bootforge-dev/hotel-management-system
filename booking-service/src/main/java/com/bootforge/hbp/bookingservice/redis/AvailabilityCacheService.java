package com.bootforge.hbp.bookingservice.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AvailabilityCacheService {
    private final RedisTemplate<String, Object> redisTemplate;

    private static final Duration TTL = Duration.ofSeconds(30);

    public Boolean get(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        String key = RedisKeyUtil.availabilityKey(roomId, checkIn, checkOut);
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) return null;
        return (Boolean) value;
    }

    public void put(Long roomId, LocalDate checkIn, LocalDate checkOut, boolean available){
        String key = RedisKeyUtil.availabilityKey(roomId, checkIn, checkOut);
        redisTemplate.opsForValue().set(key, available, TTL);
    }

    public  void evict(Long roomId, LocalDate checkIn, LocalDate checkOut){
        String key = RedisKeyUtil.availabilityKey(roomId, checkIn, checkOut);
        redisTemplate.delete(key);
    }
}
