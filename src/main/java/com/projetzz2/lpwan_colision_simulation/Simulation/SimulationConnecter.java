package com.projetzz2.lpwan_colision_simulation.Simulation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.projetzz2.lpwan_colision_simulation.Simulation.lpwan_collision_simulation.src.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class SimulationConnecter extends ModelRunner{

    private boolean simulationRunning = false;
    private int simulationMessagePerSecond;

    private boolean MiotyModelRun;
    private ArrayList<FrameModel> MiotyFrames = new ArrayList<FrameModel>();
    private boolean SigfoxModelRun;
    private ArrayList<FrameModel> SigfoxFrames = new ArrayList<FrameModel>();
    private boolean LoRaWanRun;
    private ArrayList<FrameModel> LoRaWanFrames = new ArrayList<FrameModel>();

    SimulationConnecter(){
        simulationMessagePerSecond = 2;
        MiotyModelRun = true;
        SigfoxModelRun = true;
        LoRaWanRun = true;
    }

    public boolean isSimulationRunning() {
        return simulationRunning;
    }



    public void setSimulationRunning(boolean simulationRunning) {
        this.simulationRunning = simulationRunning;
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



    public ArrayList<FrameModel> getMiotyFrames() {
        return MiotyFrames;
    }



    public void setMiotyFrames(ArrayList<FrameModel> miotyFrames) {
        MiotyFrames = miotyFrames;
    }



    public boolean isSigfoxModelRun() {
        return SigfoxModelRun;
    }



    public void setSigfoxModelRun(boolean sigfoxModelRun) {
        SigfoxModelRun = sigfoxModelRun;
    }



    public ArrayList<FrameModel> getSigfoxFrames() {
        return SigfoxFrames;
    }



    public void setSigfoxFrames(ArrayList<FrameModel> sigfoxFrames) {
        SigfoxFrames = sigfoxFrames;
    }



    public boolean isLoRaWanRun() {
        return LoRaWanRun;
    }



    public void setLoRaWanRun(boolean loRaWanRun) {
        LoRaWanRun = loRaWanRun;
    }



    public ArrayList<FrameModel> getLoRaWanFrames() {
        return LoRaWanFrames;
    }



    public void setLoRaWanFrames(ArrayList<FrameModel> loRaWanFrames) {
        LoRaWanFrames = loRaWanFrames;
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