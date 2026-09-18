package com.bootforge.hbp.bookingservice.service;

import com.bootforge.hbp.bookingservice.redis.RedisKeyUtil;
import com.bootforge.hbp.common.dto.booking.BookingResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private static final Duration TTL = Duration.ofMinutes(10);


    // ============================================================
    // START PROCESSING
    // ============================================================

    public boolean tryStartProcessing(String idempotencyKey) {
        String key = RedisKeyUtil.idempotencyKey(idempotencyKey);
        Boolean acquired = redisTemplate.opsForValue().setIfAbsent(key, "PROCESSING", TTL);
        return Boolean.TRUE.equals(acquired);
    }


    // ============================================================
    // MARK COMPLETED
    // ============================================================

    public void markCompleted(String idempotencyKey, BookingResponse response) {
        String key = RedisKeyUtil.idempotencyKey(idempotencyKey);

        try {
            String json = objectMapper.writeValueAsString(response);
            redisTemplate.opsForValue().set(key, json, TTL);

        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize idempotency response", e);
        }
    }


    // ============================================================
    // GET COMPLETED RESPONSE
    // ============================================================

    public BookingResponse getCompletedResponse(String idempotencyKey) {

        String key = RedisKeyUtil.idempotencyKey(idempotencyKey);
        Object value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            return null;
        }

        if ("PROCESSING".equals(value.toString())) {
            return null;
        }

        try {
            return objectMapper.readValue(value.toString(), BookingResponse.class);

        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to deserialize idempotency response", e
            );
        }
    }


    // ============================================================
    // REMOVE
    // ============================================================

    public void remove(String idempotencyKey) {

        String key = RedisKeyUtil.idempotencyKey(idempotencyKey);
        redisTemplate.delete(key);
    }
}