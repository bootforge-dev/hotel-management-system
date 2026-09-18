package com.bootforge.hbp.bookingservice.redis;

import java.time.LocalDate;

public final class RedisKeyUtil {
    private RedisKeyUtil() {

    }

    public static String availabilityKey(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        return String.format("room:availability:%d:%s:%s", roomId, checkIn, checkOut);
    }

    public static String lockKey(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        return String.format("lock:availability:%d:%s:%s", roomId, checkIn, checkOut);
    }

    public static String idempotencyKey(String key) {
        return "idempotency:booking" + key;
    }
}
