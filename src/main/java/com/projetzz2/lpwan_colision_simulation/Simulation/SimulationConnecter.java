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


    private ArrayList<FrameModel> genFrameModel(RadioModel r){
        ArrayList<FrameModel> allFrames = new ArrayList<FrameModel>();

        // Generate 10x more frame per second on a 10s time range
        for ( int i = 0 ; i < simulationMessagePerSecond * 10 ; i++ ) {
            // get a starting time in us
            long start = (long)Math.floor(Math.random()*RUN_DURATION_S*1_000_000);
            // Generate the frames
            ArrayList<FrameModel> fms = r.getFrameModel(start);
            for ( FrameModel fm : fms ) {
                // only keep what happen on the seconds 1st & 9th and for every ms link the frame
                // to this slot time.
                if ( fm.getUsEnd() < WINDOW_START_S*1_000_000 || fm.getUsStart() > WINDOW_STOP_S*1_000_000 ) continue;
                
                allFrames.add(fm);
            }
        }

        return allFrames;
    }

    public void simulationRun(){
        simulationRunning = true;
        RadioModel r = new MiotyModel(MiotyModel.MODE_EU1);
        RadioModel s = new SigfoxModel();
        RadioModel l = new LoRaWanModel();

        if(MiotyModelRun){
            // Run Mioty
            MiotyFrames.clear();
            MiotyFrames = genFrameModel(r);
        }

        if(SigfoxModelRun){
            // Run sigfox
            SigfoxFrames.clear();
            SigfoxFrames = genFrameModel(s);
        }

        if(LoRaWanRun){
            // Run LoRaWan
            LoRaWanFrames.clear();
            MiotyFrames = genFrameModel(l);
        }
        

        simulationRunning = false;
    }

    public void freeSimulation(){
        MiotyFrames.clear();
        SigfoxFrames.clear();
        LoRaWanFrames.clear();
    }
}