package com.contentdiscovery.recommendation.infrastructure.adapter.in.web.controller;

import com.contentdiscovery.recommendation.domain.exception.*;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({UserNotFoundException.class, VideoNotFoundException.class})
    ResponseEntity<Map<String, Object>> notFound(RuntimeException exception, HttpServletRequest request) {
        String code = exception instanceof UserNotFoundException ? "USER_NOT_FOUND" : "VIDEO_NOT_FOUND";
        return response(HttpStatus.NOT_FOUND, code, exception.getMessage(), request.getRequestURI());
    }
    @ExceptionHandler({InvalidLimitException.class, IllegalArgumentException.class,
            MethodArgumentNotValidException.class, ConstraintViolationException.class,
            MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class,
            MissingPathVariableException.class, MissingServletRequestParameterException.class})
    ResponseEntity<Map<String, Object>> badRequest(Exception exception, HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", exception.getMessage(),
                request.getRequestURI());
    }

    private ResponseEntity<Map<String, Object>> response(HttpStatus status, String code, String message,
                                                         String path) {
        return ResponseEntity.status(status).body(Map.of(
                "timestamp", Instant.now(),
                "status", status.value(),
                "code", code,
                "message", message == null ? status.getReasonPhrase() : message,
                "path", path));
    }
}
