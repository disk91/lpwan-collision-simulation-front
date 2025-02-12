package com.projetzz2.lpwan_colision_simulation.models;

public class SimulationServerAPIParameters {
    private int singal_length;
    private int value_size;

    public SimulationServerAPIParameters(){}

    public int getSignal_length(){
        return singal_length;
    }

    public int getValue_size(){
        return value_size;
    }

    public void setSignal_length(int singal_length){
        this.singal_length = singal_length;
    }

    public void setValue_size(int value_size){
        this.value_size = value_size;
    }
}
