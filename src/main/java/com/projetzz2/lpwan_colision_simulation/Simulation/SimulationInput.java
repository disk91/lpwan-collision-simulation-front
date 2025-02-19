package com.projetzz2.lpwan_colision_simulation.Simulation;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SimulationInput {

    private int simulationMessagePerSecond = 2;
    private boolean MiotyModelRun = true;
    private boolean SigfoxModelRun = true;
    private boolean LoRaWanModelRun = true;


    SimulationInput(){
        simulationMessagePerSecond = 2;
        MiotyModelRun = true;
        SigfoxModelRun = true;
        LoRaWanModelRun = true;
    }

    @JsonProperty("simulationMessagePerSecond")
    public int getSimulationMessagePerSecond() {
        return simulationMessagePerSecond;
    }


    public void setSimulationMessagePerSecond(int simulationMessagePerSecond) {
        this.simulationMessagePerSecond = simulationMessagePerSecond;
    }

    @JsonProperty("MiotyModelRun")
    public boolean isMiotyModelRun() {
        return MiotyModelRun;
    }

    
    public void setMiotyModelRun(boolean miotyModelRun) {
        MiotyModelRun = miotyModelRun;
    }

    @JsonProperty("SigfoxModelRun")
    public boolean isSigfoxModelRun() {
        return SigfoxModelRun;
    }


    public void setSigfoxModelRun(boolean sigfoxModelRun) {
        SigfoxModelRun = sigfoxModelRun;
    }

    @JsonProperty("LoRaWanModelRun")
    public boolean isLoRaWanModelRun() {
        return LoRaWanModelRun;
    }

    
    public void setLoRaWanModelRun(boolean loRaWanModelRun) {
        LoRaWanModelRun = loRaWanModelRun;
    }


    @Override
    public String toString(){
        return "Number of messages per seconds: " + simulationMessagePerSecond + ", Run LoRaWan: " + 
                LoRaWanModelRun + ", Run Mioty: " + MiotyModelRun + ", Run Sigfox: " + SigfoxModelRun;
    }
}
