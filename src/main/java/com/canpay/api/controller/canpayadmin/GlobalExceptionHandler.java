package com.canpay.api.controller.canpayadmin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Global exception handler for the canpayadmin controllers.
 * Handles specific exceptions and returns appropriate HTTP responses.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Handles IllegalArgumentException and returns a 400 Bad Request response.
     * 
     * @param ex the exception thrown
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", ex.getMessage()));
    }

    /**
     * Handles NoSuchElementException and returns a 404 Not Found response.
     * 
     * @param ex the exception thrown
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNoSuchElementException(NoSuchElementException ex) {
        return ResponseEntity.status(404).body(Map.of(
                "success", false,
                "message", ex.getMessage()));
    }
}
