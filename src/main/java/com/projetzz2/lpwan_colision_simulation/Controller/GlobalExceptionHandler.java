package com.projetzz2.lpwan_colision_simulation.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.projetzz2.lpwan_colision_simulation.ServerLog;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SimulationNotFoundException.class)
    public ResponseEntity<String> handleSimulationNotFound(SimulationNotFoundException ex) {
        ServerLog.printErrorLog("Error simulation id unknown: " + ex.getSimulationId());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        ex.printStackTrace();
        return new ResponseEntity<>("{\n  java error: " + ex.getMessage() + "\n}", HttpStatus.BAD_REQUEST);
    }
}