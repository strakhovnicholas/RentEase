package ru.practicum.shareit.gateway.special.exception.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.gateway.special.exception.AccessDeniedException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(
            AccessDeniedException ex) {

        log.warn("Access denied: {}", ex.getMessage());

        Map<String, Object> errorBody = new LinkedHashMap<>();
        errorBody.put("error", "ACCESS_DENIED");
        errorBody.put("message", ex.getMessage());
        errorBody.put("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorBody);
    }
}