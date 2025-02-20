/**
 * SimulationNotFoundException.java
 *
 * This exception is thrown when a simulation with a specified SimulationId is not found.
 * It extends RuntimeException and carries the missing SimulationId. The exception message
 * is formatted as a JSON string indicating the unknown id.
 */

 package com.projetzz2.lpwan_colision_simulation.Controller;

 import com.projetzz2.lpwan_colision_simulation.Simulation.SimulationId;
 
 public class SimulationNotFoundException extends RuntimeException {
 
     // The SimulationId that was not found.
     private SimulationId id;
 
     /**
      * Constructs a new SimulationNotFoundException with the specified missing SimulationId.
      * The exception message is formatted to indicate the unknown id.
      *
      * @param id The SimulationId that was not found.
      */
     public SimulationNotFoundException(SimulationId id) {
         super("{\"Unknown id\": " + id + "}");
         this.id = id;
     }
 
     /**
      * Returns the SimulationId associated with this exception.
      *
      * @return The SimulationId that was not found.
      */
     public SimulationId getSimulationId(){
         return id;
     }
 }
 
