package com.bootforge.hbp.bookingservice.service;

import com.bootforge.hbp.bookingservice.redis.RedisKeyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DistributedLockService {

    private final RedisTemplate<String, Object> redisTemplate;

    public boolean tryLock(Long roomId, LocalDate checkIn, LocalDate checkOut, String lockValue) {
        String key = RedisKeyUtil.lockKey(roomId, checkIn, checkOut);
        Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(key, lockValue, Duration.ofSeconds(10));
        return Boolean.TRUE.equals(acquired);
    }

    public void unlock(Long roomId, LocalDate checkIn, LocalDate checkOut, String lockValue) {
        String key = RedisKeyUtil.lockKey(roomId, checkIn, checkOut);
        Object currentValue = redisTemplate.opsForValue().get(key);
        if (lockValue.equals(currentValue)) {
            redisTemplate.delete(key);
        }
    }

}
