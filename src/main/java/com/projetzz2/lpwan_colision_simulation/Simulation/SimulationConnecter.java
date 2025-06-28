/**
 * SimulationConnecter.java
 *
 * This class extends ModelRunner to execute radio simulations based on the provided simulation input.
 * It manages simulation execution for different radio models (Mioty, Sigfox, and LoRaWan) by running
 * simulation steps, processing the resulting frame chains, and updating their channel frequencies.
 *
 * The simulation input determines which radio models are active, and for each active model, the corresponding
 * frames are generated, processed (removing head references), and their frequencies updated based on specific
 * rules.
 *
 * JSON annotations (@JsonProperty) are used to define how the properties are serialized/deserialized.
 */

 package com.projetzz2.lpwan_colision_simulation.Simulation;

 import java.util.ArrayList;
 
 import com.fasterxml.jackson.annotation.JsonProperty;
 import com.projetzz2.lpwan_colision_simulation.Simulation.lpwan_collision_simulation.src.*;
 
 public class SimulationConnecter extends ModelRunner {
 
     // Indicates whether the simulation is currently running.
     private boolean simulationRunning = false;
     // The input parameters for the simulation.
     private SimulationInput input;
     
     // The Mioty mode used for frequency calculations.
     private int MiotyMode;
     // Lists to store simulation frame chains for each radio model.
     private ArrayList<FrameModel> MiotyFrames = new ArrayList<FrameModel>();
     private ArrayList<FrameModel> SigfoxFrames = new ArrayList<FrameModel>();
     private ArrayList<FrameModel> LoRaWanFrames = new ArrayList<FrameModel>();
 
     /**
      * Constructor that initializes the simulation with the given input parameters.
      *
      * @param input The simulation input configuration.
      */
     public SimulationConnecter(SimulationInput input) {
         this.input = input;
     }
 
     /**
      * Returns whether the simulation is running.
      *
      * @return true if the simulation is running, false otherwise.
      */
     @JsonProperty("simulationRunning")
     public boolean isSimulationRunning() {
         return simulationRunning;
     }
 
     /**
      * Sets the simulation running status.
      *
      * @param simulationRunning true to indicate the simulation is running.
      */
     public void setSimulationRunning(boolean simulationRunning) {
         this.simulationRunning = simulationRunning;
     }
 
     /**
      * Retrieves the simulation input parameters.
      *
      * @return the simulation input.
      */
     @JsonProperty("input")
     public SimulationInput getInput() {
         return input;
     }
 
     /**
      * Updates the simulation input parameters.
      *
      * @param input the new simulation input.
      */
     public void setInput(SimulationInput input) {
         this.input = input;
     }
 
     /**
      * Returns the list of Mioty frames generated during simulation.
      *
      * @return an ArrayList of FrameModel objects for Mioty.
      */
     @JsonProperty("MiotyFrames")
     public ArrayList<FrameModel> getMiotyFrames() {
         return MiotyFrames;
     }
 
     /**
      * Sets the list of Mioty frames.
      *
      * @param miotyFrames the list of FrameModel objects to set.
      */
     public void setMiotyFrames(ArrayList<FrameModel> miotyFrames) {
         MiotyFrames = miotyFrames;
     }
 
     /**
      * Returns the list of Sigfox frames generated during simulation.
      *
      * @return an ArrayList of FrameModel objects for Sigfox.
      */
     @JsonProperty("SigfoxFrames")
     public ArrayList<FrameModel> getSigfoxFrames() {
         return SigfoxFrames;
     }
 
     /**
      * Sets the list of Sigfox frames.
      *
      * @param sigfoxFrames the list of FrameModel objects to set.
      */
     public void setSigfoxFrames(ArrayList<FrameModel> sigfoxFrames) {
         SigfoxFrames = sigfoxFrames;
     }
 
     /**
      * Returns the list of LoRaWan frames generated during simulation.
      *
      * @return an ArrayList of FrameModel objects for LoRaWan.
      */
     @JsonProperty("LoRaWanFrames")
     public ArrayList<FrameModel> getLoRaWanFrames() {
         return LoRaWanFrames;
     }
 
     /**
      * Sets the list of LoRaWan frames.
      *
      * @param loRaWanFrames the list of FrameModel objects to set.
      */
     public void setLoRaWanFrames(ArrayList<FrameModel> loRaWanFrames) {
         LoRaWanFrames = loRaWanFrames;
     }
 
     /**
      * Runs the simulation by executing each active radio model simulation.
      * It performs the following for each model:
      *   - Clears existing frames.
      *   - Runs the simulation step to generate new frames.
      *   - Processes the frame chain by removing head references.
      *   - Updates the channel frequencies based on model-specific rules.
      */
     public void simulationRun() {
         simulationRunning = true;
         // Set the Mioty mode; here it is hard-coded to MODE_EU1.
         MiotyMode = MiotyModel.MODE_EU1;
         // Create radio model instances for Mioty, Sigfox, and LoRaWan.
         RadioModel r = new MiotyModel(MiotyMode);
         RadioModel s = new SigfoxModel();
         RadioModel l = new LoRaWanModel();
 
         // Run Mioty simulation if enabled.
         if (input.isMiotyModelRun()) {
             MiotyFrames.clear();
             System.out.println("Running Mioty with " + input.getSimulationMessagePerSecond() + "msg/s");
             // Execute simulation step for Mioty.
             MiotyFrames = runStep(r, input.getSimulationMessagePerSecond());
             // Remove head references from the frame chain.
             removeHeadFrameModel(MiotyFrames);
             // Update frequencies based on the Mioty mode.
             updateMiotyFrequencies();
         }
 
         // Run Sigfox simulation if enabled.
         if (input.isSigfoxModelRun()) {
             SigfoxFrames.clear();
             System.out.println("Running Sigfox with " + input.getSimulationMessagePerSecond() + "msg/s");
             // Execute simulation step for Sigfox.
             SigfoxFrames = runStep(s, input.getSimulationMessagePerSecond());
             // Remove head references from the frame chain.
             removeHeadFrameModel(SigfoxFrames);
             // Update channel frequencies for Sigfox.
             updateSigfoxFrequencies();
         }
 
         // Run LoRaWan simulation if enabled.
         if (input.isLoRaWanModelRun()) {
             LoRaWanFrames.clear();
             System.out.println("Running LoRaWan with " + input.getSimulationMessagePerSecond() + "msg/s");
             // Execute simulation step for LoRaWan.
             LoRaWanFrames = runStep(l, input.getSimulationMessagePerSecond());
             // Remove head references from the frame chain.
             removeHeadFrameModel(LoRaWanFrames);
             // Update channel frequencies for LoRaWan.
             updateLoRaWanFrequencies();
         }
         
         // Mark the simulation as finished.
         simulationRunning = false;
     }
 
     /**
      * Frees the simulation by clearing all frame lists.
      */
     public void freeSimulation() {
         MiotyFrames.clear();
         SigfoxFrames.clear();
         LoRaWanFrames.clear();
     }
 
     /**
      * Removes the head reference in each frame of the provided list.
      * For each FrameModel in the list, iterates through its linked chain and sets the head to null.
      *
      * @param fms the list of FrameModel objects to process.
      */
     private void removeHeadFrameModel(ArrayList<FrameModel> fms) {
         for (FrameModel fm : fms) {
             FrameModel fmNext = fm;
             while (fmNext != null) {
                 // Remove the head reference to break potential cyclic links.
                 fmNext.setHead(null);
                 fmNext = fmNext.getNext();
             }
         }
     }
 
     /**
      * Updates the channel frequencies for Mioty frames based on the current Mioty mode and channel values.
      * For MODE_EU0 and MODE_EU1, different calculations are applied.
      */
     private void updateMiotyFrequencies() {
         for (FrameModel fm : MiotyFrames) {
             FrameModel fmNext = fm;
             while (fmNext != null) {
                 int channel = fmNext.getChannel();
 
                 if (MiotyMode == MiotyModel.MODE_EU0 && channel >= -5 && channel <= 28) {
                     // Calculate frequency for MODE_EU0 => center is 868.18MHz
                     // Channel size is 2.5KHz
                     int frequency = (channel - 12) * 2380 + 868180000;
                     fmNext.setChannel(frequency);
                 } else if (MiotyMode == MiotyModel.MODE_EU1 && channel >= -5 && channel <= 68) {
                     int frequency = 0;

                     // The frequancy band depends on the channel group, in EU1 we used channel over 40 (minus offset delta)
                     // to separate the two bands
                     if (channel < 30) {
                         // Calculate frequency for lower channel values in MODE_EU1.
                         frequency = (channel - 12) * 2380 + 868180000;
                     } else {
                         // Calculate frequency for higher channel values in MODE_EU1.
                         frequency = (channel - 12 - 40) * 2380 + 868080000;
                     }
 
                     fmNext.setChannel(frequency);
                 }
 
                 fmNext = fmNext.getNext();
             }
         }
     }
 
     /**
      * Updates the channel frequencies for Sigfox frames.
      * For each frame, if the channel value is within the valid range, the frequency is calculated and updated.
      */
     private void updateSigfoxFrequencies() {
         for (FrameModel fm : SigfoxFrames) {
             FrameModel fmNext = fm;
             while (fmNext != null) {
                 int channel = fmNext.getChannel();
                 
                 if (channel >= 0 && channel <= 1920) {
                     // Calculate frequency for Sigfox channels.
                     int frequency = 868034000 + 100 * channel;
                     fmNext.setChannel(frequency);
                 }
 
                 fmNext = fmNext.getNext();
             }
         }
     }
 
     /**
      * Updates the channel frequencies for LoRaWan frames.
      * Uses a predefined array of EU frequencies to set the channel frequency based on the channel index.
      */
     private void updateLoRaWanFrequencies() {
 
         for (FrameModel fm : LoRaWanFrames) {
             FrameModel fmNext = fm;
             while (fmNext != null) {
                 int channel = fmNext.getChannel();
 
                 if (channel >= 0 && channel <= 7) {
                     // Calculate frequency for LoRaWan channels.
                     int frequency = 867100000 + 200000 * fmNext.getChannel();
                     fmNext.setChannel(frequency);
                 }
                 
                 fmNext = fmNext.getNext();
             }
         }
     }
 }
 