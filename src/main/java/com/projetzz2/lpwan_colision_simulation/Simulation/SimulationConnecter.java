package com.projetzz2.lpwan_colision_simulation.Simulation;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.projetzz2.lpwan_colision_simulation.Simulation.lpwan_collision_simulation.src.*;


public class SimulationConnecter extends ModelRunner{


    private boolean simulationRunning = false;
    private SimulationInput input;
    

    private ArrayList<FrameModel> MiotyFrames = new ArrayList<FrameModel>();
    private ArrayList<FrameModel> SigfoxFrames = new ArrayList<FrameModel>();
    private ArrayList<FrameModel> LoRaWanFrames = new ArrayList<FrameModel>();

    public SimulationConnecter(SimulationInput input){
        this.input = input;
    }

    @JsonProperty("simulationRunning")
    public boolean isSimulationRunning() {
        return simulationRunning;
    }


    public void setSimulationRunning(boolean simulationRunning) {
        this.simulationRunning = simulationRunning;
    }

    @JsonProperty("input")
    public SimulationInput getInput() {
        return input;
    }

    public void setInput(SimulationInput input) {
        this.input = input;
    }

    @JsonProperty("MiotyFrames")
    public ArrayList<FrameModel> getMiotyFrames() {
        return MiotyFrames;
    }


    public void setMiotyFrames(ArrayList<FrameModel> miotyFrames) {
        MiotyFrames = miotyFrames;
    }

    @JsonProperty("SigfoxFrames")
    public ArrayList<FrameModel> getSigfoxFrames() {
        return SigfoxFrames;
    }


    public void setSigfoxFrames(ArrayList<FrameModel> sigfoxFrames) {
        SigfoxFrames = sigfoxFrames;
    }

    @JsonProperty("LoRaWanFrames")
    public ArrayList<FrameModel> getLoRaWanFrames() {
        return LoRaWanFrames;
    }


    public void setLoRaWanFrames(ArrayList<FrameModel> loRaWanFrames) {
        LoRaWanFrames = loRaWanFrames;
    }



    public void simulationRun(){
        simulationRunning = true;
        RadioModel r = new MiotyModel(MiotyModel.MODE_EU1);
        RadioModel s = new SigfoxModel();
        RadioModel l = new LoRaWanModel();

        if(input.isMiotyModelRun()){
            // Run Mioty
            MiotyFrames.clear();
            System.out.println("Running Mioty with " + input.getSimulationMessagePerSecond() + "msg/s");
            MiotyFrames = runStep(r, input.getSimulationMessagePerSecond() );
            removeHeadFrameModel(MiotyFrames);
            updateMiotyFrequencies();
        }

        if(input.isSigfoxModelRun()){
            // Run sigfox
            SigfoxFrames.clear();
            System.out.println("Running Sigfox with " + input.getSimulationMessagePerSecond()  + "msg/s");
            SigfoxFrames = runStep(s, input.getSimulationMessagePerSecond() );
            removeHeadFrameModel(SigfoxFrames);
            updateSigfoxFrequencies();
        }

        if(input.isLoRaWanModelRun()){
            // Run LoRaWan
            LoRaWanFrames.clear();
            System.out.println("Running LoRaWan with " + input.getSimulationMessagePerSecond()  + "msg/s");
            LoRaWanFrames = runStep(l, input.getSimulationMessagePerSecond() );
            removeHeadFrameModel(LoRaWanFrames);
            updateLoRaWanFrequencies();
        }
        

        simulationRunning = false;
    }

    public void freeSimulation(){
        MiotyFrames.clear();
        SigfoxFrames.clear();
        LoRaWanFrames.clear();
    }


    private void removeHeadFrameModel(ArrayList<FrameModel> fms){
        for ( FrameModel fm : fms ) {
            FrameModel fmNext = fm;
            while(fmNext != null){
                fmNext.setHead(null);
                fmNext = fmNext.getNext();
            }
        }
    }

    private void updateMiotyFrequencies(){
        // Pour l'iinstant RIen
    }

    private void updateSigfoxFrequencies(){
        for (FrameModel fm : SigfoxFrames) {
            FrameModel fmNext = fm;
            while(fmNext != null){
                int channel = fmNext.getChannel();
                
                if(channel <= 1920 && channel >= 0){
                    int frequency = 868034000 + 100*channel;
                    System.out.println("frequency: " + frequency + ", channel: " + channel);
                    fmNext.setChannel(frequency);
                }

                fmNext = fmNext.getNext();
            }
        }
    }

    private void updateLoRaWanFrequencies(){
        int EUFrequencices[] = {868100000, 868300000, 868500000, 867100000, 867300000, 867500000, 867700000, 867900000};

        for (FrameModel fm : LoRaWanFrames) {
            FrameModel fmNext = fm;
            while(fmNext != null){
                int channel = fmNext.getChannel();

                if(channel >= 0 && channel <= 7){
                    int frequency = EUFrequencices[fmNext.getChannel()];
                    fmNext.setChannel(frequency);
                }
                
                fmNext = fmNext.getNext();
            }
        }
    }
}