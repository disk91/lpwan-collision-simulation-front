package com.projetzz2.lpwan_colision_simulation.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        ex.printStackTrace();  // Affiche la trace de l'erreur dans la console
        return new ResponseEntity<>("{\n  java error: " + ex.getMessage() + "\n}", HttpStatus.BAD_REQUEST);
    }
}