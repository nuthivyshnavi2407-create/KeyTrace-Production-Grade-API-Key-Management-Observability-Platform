package com.vyshnavi.keytrace.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex) {
        // Return a clean JSON 500 instead of the Whitelabel error page
        return ResponseEntity.status(500).body("{\"error\": \"Internal Server Error: " + ex.getMessage() + "\"}");
    }
}