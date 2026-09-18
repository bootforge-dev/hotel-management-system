package com.bootforge.hbp.bookingservice.exception;

import com.bootforge.hbp.bookingservice.resilience.exception.HotelServiceUnavailableException;
import com.bootforge.hbp.bookingservice.resilience.exception.RoomServiceUnavailableException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class,IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(build(
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        "Validation failed",
                        request.getRequestURI(),
                        errors));

    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(build(
                        HttpStatus.NOT_FOUND.value(),
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        "Resource not found",
                        request.getRequestURI(),
                        null));

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception ex, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(build(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                        "Internal Server Error",
                        request.getRequestURI(),
                        null));

    }

    @ExceptionHandler({
            HotelServiceUnavailableException.class,
            RoomServiceUnavailableException.class
    })
    public ResponseEntity<ErrorResponse> handleServiceUnavailable(
            RuntimeException ex,
            HttpServletRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(
                        build(
                                HttpStatus.SERVICE_UNAVAILABLE.value(),
                                HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase(),
                                ex.getMessage(),
                                request.getRequestURI(),
                                null
                        )
                );
    }

    private ErrorResponse build(Integer status, String error, String message, String path, Map<String, String> errors) {
        return ErrorResponse.builder()
                .status(status)
                .error(error)
                .message(message)
                .path(path)
                .errors(errors)
                .build();
    }
}
