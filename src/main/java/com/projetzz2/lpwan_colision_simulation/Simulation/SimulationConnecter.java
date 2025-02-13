package com.projetzz2.lpwan_colision_simulation.Simulation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.projetzz2.lpwan_colision_simulation.Simulation.lpwan_collision_simulation.src.*;

public class SimulationConnecter extends ModelRunner{

    private boolean simulationRunning = false;
    int simulationMessagePerSecond = 2;

    private boolean MiotyModelRun = true;
    private ArrayList<FrameModel> MiotyFrames = new ArrayList<FrameModel>();
    private boolean SigfoxModelRun = true;
    private ArrayList<FrameModel> SigfoxFrames = new ArrayList<FrameModel>();
    private boolean LoRaWanRun = true;
    private ArrayList<FrameModel> LoRaWanFrames = new ArrayList<FrameModel>();


    public boolean isSimulationRunning() {
        return simulationRunning;
    }

    public ArrayList<FrameModel> getMiotyFrames() {
        return MiotyFrames;
    }

    public ArrayList<FrameModel> getSigfoxFrames() {
        return SigfoxFrames;
    }

    public ArrayList<FrameModel> getLoRaWanFrames() {
        return LoRaWanFrames;
    }

    public int getSimulationMessagePerSecond() {
        return simulationMessagePerSecond;
    }

    public void setSimulationMessagePerSecond(int simulationMessagePerSecond) {
        this.simulationMessagePerSecond = simulationMessagePerSecond;
    }

    public boolean isMiotyModelRun() {
        return MiotyModelRun;
    }

    public void setMiotyModelRun(boolean miotyModelRun) {
        MiotyModelRun = miotyModelRun;
    }

    public boolean isSigfoxModelRun() {
        return SigfoxModelRun;
    }

    public void setSigfoxModelRun(boolean sigfoxModelRun) {
        SigfoxModelRun = sigfoxModelRun;
    }

    public boolean isLoRaWanRun() {
        return LoRaWanRun;
    }

    public void setLoRaWanRun(boolean loRaWanRun) {
        LoRaWanRun = loRaWanRun;
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

    public void simulationRun(){
        simulationRunning = true;
        RadioModel r = new MiotyModel(MiotyModel.MODE_EU1);
        RadioModel s = new SigfoxModel();
        RadioModel l = new LoRaWanModel();

        if(MiotyModelRun){
            // Run Mioty
            MiotyFrames.clear();
            MiotyFrames = runStep(r, simulationMessagePerSecond);
            removeHeadFrameModel(MiotyFrames);
        }

        if(SigfoxModelRun){
            // Run sigfox
            SigfoxFrames.clear();
            SigfoxFrames = runStep(s, simulationMessagePerSecond);
            removeHeadFrameModel(SigfoxFrames);
        }

        if(LoRaWanRun){
            // Run LoRaWan
            LoRaWanFrames.clear();
            LoRaWanFrames = runStep(l, simulationMessagePerSecond);
            removeHeadFrameModel(LoRaWanFrames);
        }
        

        simulationRunning = false;
    }

    public void freeSimulation(){
        MiotyFrames.clear();
        SigfoxFrames.clear();
        LoRaWanFrames.clear();
    }
}