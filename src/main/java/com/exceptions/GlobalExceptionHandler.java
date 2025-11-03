package com.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.OffsetDateTime;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends RuntimeException {

    @ExceptionHandler(RegistroEmbarqueException.class)
    public ResponseEntity<Map<String, Object>> handleRegistroEmbarqueException(RegistroEmbarqueException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of(
                "erro", "Falha no registro de embarque",
                "mensagem", ex.getMessage(),
                "timestamp", OffsetDateTime.now()
        ));
    }
}