package com.canpay.api.controller.canpayadmin;

import com.canpay.api.entity.ResponseEntityBuilder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
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

    /**
     * Handles NullPointerException and returns a 500 Internal Server Error
     * response.
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> handleNullPointerException(NullPointerException ex) {
        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                .resultMessage("Null pointer exception: " + ex.getMessage())
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Null pointer exception", "details", ex.getMessage()))
                .buildWrapped();
    }

    /**
     * Handles MethodArgumentNotValidException and returns a 400 Bad Request
     * response.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                .resultMessage("Validation failed")
                .httpStatus(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Validation failed", "details", ex.getBindingResult().toString()))
                .buildWrapped();
    }

    /**
     * Handles HttpRequestMethodNotSupportedException and returns a 405 Method Not
     * Allowed response.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                .resultMessage("Method not allowed: " + ex.getMessage())
                .httpStatus(HttpStatus.METHOD_NOT_ALLOWED)
                .body(Map.of("error", "Method not allowed", "details", ex.getMessage()))
                .buildWrapped();
    }

    /**
     * Handles HttpMessageNotReadableException and returns a 400 Bad Request
     * response.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                .resultMessage("Malformed JSON request: " + ex.getMessage())
                .httpStatus(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Malformed JSON request", "details", ex.getMessage()))
                .buildWrapped();
    }

    /**
     * Handles AccessDeniedException and returns a 403 Forbidden response.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex) {
        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                .resultMessage("Access denied: " + ex.getMessage())
                .httpStatus(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Access denied", "details", ex.getMessage()))
                .buildWrapped();
    }

    /**
     * Handles DataIntegrityViolationException and returns a 409 Conflict response.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                .resultMessage("Data integrity violation: " + ex.getMessage())
                .httpStatus(HttpStatus.CONFLICT)
                .body(Map.of("error", "Data integrity violation", "details", ex.getMessage()))
                .buildWrapped();
    }

    /**
     * Handles MissingServletRequestParameterException and returns a 400 Bad Request
     * response.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                .resultMessage("Missing request parameter: " + ex.getMessage())
                .httpStatus(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Missing request parameter", "details", ex.getMessage()))
                .buildWrapped();
    }

    /**
     * Handles RuntimeException and returns a 500 Internal Server Error response.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                .resultMessage("Runtime exception: " + ex.getMessage())
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Runtime exception", "details", ex.getMessage()))
                .buildWrapped();
    }

    /**
     * Handles generic Exception and returns a 500 Internal Server Error response.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex) {
        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                .resultMessage("Internal server error: " + ex.getMessage())
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal server error", "details", ex.getMessage()))
                .buildWrapped();
    }

    /**
     * Handles all other Throwables and returns a 500 Internal Server Error
     * response.
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<?> handleThrowable(Throwable ex) {
        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                .resultMessage("Unexpected error: " + ex.getMessage())
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Unexpected error", "details", ex.getMessage()))
                .buildWrapped();
    }
}
