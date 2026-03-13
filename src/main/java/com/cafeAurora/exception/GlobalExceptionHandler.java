package com.cafeAurora.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("message", ex.getMessage());
        error.put("status", 400);

        return ResponseEntity.status(400).body(error);
    }
    
    @ExceptionHandler(IncompleteProfileException.class)
    public ResponseEntity<Map<String, Object>> handleIncompleteProfile(
            IncompleteProfileException ex) {

        Map<String, Object> body = new HashMap<>();
        body.put("message", "incomplete_profile");
        body.put("token",   ex.getToken());
        body.put("userId",  ex.getUserId());
        body.put("email",   ex.getEmail());
        body.put("name",    ex.getName());

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                             .body(body);
    }

}