package com.abhi.employeemanagement.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>>
    handleResourceNotFound(ResourceNotFoundException ex) {

        Map<String, Object> errorMap = new HashMap<>();

        errorMap.put("message", ex.getMessage());
        errorMap.put("status", HttpStatus.NOT_FOUND.value());
        errorMap.put("timestamp", LocalDateTime.now());

        return new ResponseEntity<>(errorMap,
                HttpStatus.NOT_FOUND);
    }

    
    @ExceptionHandler(
            MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>>
    handleValidationException(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> {

                    errors.put(
                            error.getField(),
                            error.getDefaultMessage()
                    );
                });

        return new ResponseEntity<>(errors,
                HttpStatus.BAD_REQUEST);
    }

   
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>>
    handleGeneralException(Exception ex) {

        Map<String, Object> errorMap = new HashMap<>();

        errorMap.put("message",
                ex.getMessage());

        errorMap.put("status",
                HttpStatus.INTERNAL_SERVER_ERROR.value());

        errorMap.put("timestamp",
                LocalDateTime.now());

        return new ResponseEntity<>(errorMap,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}