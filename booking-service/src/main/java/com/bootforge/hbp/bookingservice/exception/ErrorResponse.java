package com.bootforge.hbp.bookingservice.exception;

import lombok.Builder;

import java.util.Map;

@Builder
public record ErrorResponse(
        Integer status,
        String error,
        String message,
        String path,
        Map<String, String> errors
) {
}
