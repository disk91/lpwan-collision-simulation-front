/**
 * SimulationIdConverter.java
 *
 * This class implements the Converter interface to convert String values into SimulationId objects.
 * It parses the input string to an integer and creates a new SimulationId instance.
 * If the provided string is not a valid integer, an IllegalArgumentException is thrown.
 *
 * The @Component annotation registers this converter as a Spring bean for dependency injection.
 */

 package com.projetzz2.lpwan_colision_simulation.Simulation;

 import org.springframework.core.convert.converter.Converter;
 import org.springframework.lang.NonNull;
 import org.springframework.stereotype.Component;
 
 @Component
 public class SimulationIdConverter implements Converter<String, SimulationId> {
 
     /**
      * Converts a non-null String source to a SimulationId object.
      *
      * @param source the string representation of the SimulationId
      * @return a new SimulationId object constructed from the parsed integer
      * @throws IllegalArgumentException if the source is not a valid integer
      */
     @Override
     public SimulationId convert(@NonNull String source) {
         try {
             // Parse the input string to an integer
             int id = Integer.parseInt(source);
             // Return a new SimulationId instance with the parsed integer
             return new SimulationId(id);
         } catch (NumberFormatException e) {
             // Throw an exception if the input string cannot be parsed as an integer
             throw new IllegalArgumentException("Invalid SimulationId value: " + source, e);
         }
     }
 }
 