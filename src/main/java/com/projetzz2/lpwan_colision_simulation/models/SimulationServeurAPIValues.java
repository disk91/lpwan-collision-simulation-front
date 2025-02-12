package com.projetzz2.lpwan_colision_simulation.models;

public class SimulationServeurAPIValues {
    private float[] lorawan;
    private float[] sigfox;
    private float[] mioty;

    public SimulationServeurAPIValues(){}

    public float[] getLorawan(){
        return lorawan;
    }

    public float[] getSigfox(){
        return sigfox;
    }

    public float[] getMioty(){
        return mioty;
    }

    public void setLorawan(float[] lorawan){
        this.lorawan = lorawan;
    }

    public void setSigfox(float[] sigfox){
        this.sigfox = sigfox;
    }

    public void setMioty(float[] mioty){
        this.mioty = mioty;
    }
}
