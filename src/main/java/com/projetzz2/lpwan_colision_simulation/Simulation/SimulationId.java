/**
 * SimulationId.java
 *
 * This class represents an identifier for a simulation. It encapsulates an integer value,
 * providing a unique identifier for each simulation instance. The default constructor
 * automatically assigns a unique id using an internal static counter. Alternatively, an id
 * can be provided explicitly via an overloaded constructor.
 *
 * The class also overrides the toString, equals, and hashCode methods to facilitate proper
 * usage in collections and comparisons.
 */

 package com.projetzz2.lpwan_colision_simulation.Simulation;

 import java.util.Objects;
 
 public class SimulationId {
     
     // The unique identifier for a simulation instance.
     private int id;
     
     // Static counter used for automatic id assignment in the default constructor.
     private static int nbSimu = 0;
 
     /**
      * Default constructor that assigns a unique id based on a static counter.
      */
     public SimulationId() {
         this.id = nbSimu;
         nbSimu++;
     }
 
     /**
      * Overloaded constructor that allows setting a specific id.
      *
      * @param id the id to be assigned to this simulation instance.
      */
     public SimulationId(int id) {
         this.id = id;
     }
 
     /**
      * Retrieves the simulation id.
      *
      * @return the id of the simulation.
      */
     public int getId() {
         return id;
     }
 
     /**
      * Sets the simulation id.
      *
      * @param id the new id to set for the simulation.
      */
     public void setId(int id) {
         this.id = id;
     }
 
     /**
      * Returns the string representation of the simulation id.
      *
      * @return a string representation of the id.
      */
     @Override
     public String toString() {
         return "" + id;
     }
 
     /**
      * Checks if two SimulationId objects are equal based on their id values.
      *
      * @param o the object to compare with.
      * @return true if the objects are equal, false otherwise.
      */
     @Override
     public boolean equals(Object o) {
         // Check if both objects reference the same instance.
         if (this == o) return true;
         
         // Check if the object is null or of a different class.
         if (o == null || getClass() != o.getClass()) return false;
         
         // Cast and compare the id values.
         SimulationId that = (SimulationId) o;
         return id == that.id;
     }
 
     /**
      * Returns the hash code for this SimulationId based on its id.
      *
      * @return the hash code.
      */
     @Override
     public int hashCode() {
         return Objects.hash(id);
     }
 }
 