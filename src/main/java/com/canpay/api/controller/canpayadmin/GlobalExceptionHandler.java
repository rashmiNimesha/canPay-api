package com.canpay.api.controller.canpayadmin;

import com.canpay.api.entity.ResponseEntityBuilder;
import org.springframework.http.HttpStatus;
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
        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                .resultMessage(ex.getMessage())
                .httpStatus(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()))
                .buildWrapped();
    }

    /**
     * Handles NoSuchElementException and returns a 404 Not Found response.
     * 
     * @param ex the exception thrown
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNoSuchElementException(NoSuchElementException ex) {
        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                .resultMessage(ex.getMessage())
                .httpStatus(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()))
                .buildWrapped();
    }
}
