/**
 * SimulationInput.java
 *
 * This class represents the configuration settings for a simulation.
 * It defines the number of messages to be simulated per second as well as flags
 * to indicate whether specific network models (Mioty, Sigfox, and LoRaWan) should run.
 *
 * The class leverages Jackson annotations (@JsonProperty) to facilitate JSON
 * serialization and deserialization.
 */

 package com.projetzz2.lpwan_colision_simulation.Simulation;

 import com.fasterxml.jackson.annotation.JsonProperty;
 
 public class SimulationInput {
 
     // Number of simulation messages per second.
     private int simulationMessagePerSecond = 2;
     
     // Flags indicating whether the respective models should be executed in the simulation.
     private boolean MiotyModelRun = true;
     private boolean SigfoxModelRun = true;
     private boolean LoRaWanModelRun = true;
 
     /**
      * Default constructor initializing simulation parameters with default values.
      */
     SimulationInput() {
         simulationMessagePerSecond = 2;
         MiotyModelRun = true;
         SigfoxModelRun = true;
         LoRaWanModelRun = true;
     }
 
     /**
      * Returns the number of simulation messages per second.
      *
      * @return the simulation message frequency.
      */
     @JsonProperty("simulationMessagePerSecond")
     public int getSimulationMessagePerSecond() {
         return simulationMessagePerSecond;
     }
 
     /**
      * Sets the number of simulation messages per second.
      *
      * @param simulationMessagePerSecond the message frequency to set.
      */
     public void setSimulationMessagePerSecond(int simulationMessagePerSecond) {
         this.simulationMessagePerSecond = simulationMessagePerSecond;
     }
 
     /**
      * Indicates if the Mioty model should run.
      *
      * @return true if the Mioty model is enabled; false otherwise.
      */
     @JsonProperty("MiotyModelRun")
     public boolean isMiotyModelRun() {
         return MiotyModelRun;
     }
 
     /**
      * Sets the flag to run or not run the Mioty model.
      *
      * @param miotyModelRun the flag value to set.
      */
     public void setMiotyModelRun(boolean miotyModelRun) {
         MiotyModelRun = miotyModelRun;
     }
 
     /**
      * Indicates if the Sigfox model should run.
      *
      * @return true if the Sigfox model is enabled; false otherwise.
      */
     @JsonProperty("SigfoxModelRun")
     public boolean isSigfoxModelRun() {
         return SigfoxModelRun;
     }
 
     /**
      * Sets the flag to run or not run the Sigfox model.
      *
      * @param sigfoxModelRun the flag value to set.
      */
     public void setSigfoxModelRun(boolean sigfoxModelRun) {
         SigfoxModelRun = sigfoxModelRun;
     }
 
     /**
      * Indicates if the LoRaWan model should run.
      *
      * @return true if the LoRaWan model is enabled; false otherwise.
      */
     @JsonProperty("LoRaWanModelRun")
     public boolean isLoRaWanModelRun() {
         return LoRaWanModelRun;
     }
 
     /**
      * Sets the flag to run or not run the LoRaWan model.
      *
      * @param loRaWanModelRun the flag value to set.
      */
     public void setLoRaWanModelRun(boolean loRaWanModelRun) {
         LoRaWanModelRun = loRaWanModelRun;
     }
 
     /**
      * Returns a string representation of the simulation configuration.
      *
      * @return a summary of the simulation settings.
      */
     @Override
     public String toString() {
         return "Number of messages per seconds: " + simulationMessagePerSecond 
                 + ", Run LoRaWan: " + LoRaWanModelRun 
                 + ", Run Mioty: " + MiotyModelRun 
                 + ", Run Sigfox: " + SigfoxModelRun;
     }
 } 
