/**
 * GlobalExceptionHandler.java
 *
 * This class handles global exceptions thrown in the application.
 * It is annotated with @ControllerAdvice, allowing it to intercept exceptions thrown
 * by controllers and return custom HTTP responses.
 *
 * Two types of exceptions are handled:
 * 1. SimulationNotFoundException: Logs an error message and returns a NOT_FOUND (404) response.
 * 2. Generic Exception: Logs the exception stack trace and returns a BAD_REQUEST (400) response with the error message.
 *
 * This mechanism ensures that proper HTTP status codes and error messages are returned to the client in case of errors.
 */

 package com.projetzz2.lpwan_colision_simulation.Controller;

 import org.springframework.http.HttpStatus;
 import org.springframework.http.ResponseEntity;
 import org.springframework.web.bind.annotation.ControllerAdvice;
 import org.springframework.web.bind.annotation.ExceptionHandler;
 
 import com.projetzz2.lpwan_colision_simulation.ServerLog;
 
 @ControllerAdvice
 public class GlobalExceptionHandler {
 
     /**
      * Handles SimulationNotFoundException by logging the error and returning a 404 NOT_FOUND response.
      *
      * @param ex the SimulationNotFoundException thrown
      * @return a ResponseEntity containing the error message and HTTP status code 404
      */
     @ExceptionHandler(SimulationNotFoundException.class)
     public ResponseEntity<String> handleSimulationNotFound(SimulationNotFoundException ex) {
         // Log the error with the simulation id that was not found.
         ServerLog.printErrorLog("Error simulation id unknown: " + ex.getSimulationId());
         return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
     }
 
     /**
      * Handles all other exceptions by printing the stack trace and returning a 400 BAD_REQUEST response.
      *
      * @param ex the Exception thrown
      * @return a ResponseEntity containing the error message and HTTP status code 400
      */
     @ExceptionHandler(Exception.class)
     public ResponseEntity<String> handleException(Exception ex) {
         // Print the stack trace for debugging purposes.
         ex.printStackTrace();
         return new ResponseEntity<>("{\n  java error: " + ex.getMessage() + "\n}", HttpStatus.BAD_REQUEST);
     }
 }
 