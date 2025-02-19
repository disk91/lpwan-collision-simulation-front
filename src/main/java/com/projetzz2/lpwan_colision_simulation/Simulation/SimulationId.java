package com.projetzz2.lpwan_colision_simulation.Simulation;

import java.util.Objects;

public class SimulationId {
    
    private int id;
    private static int nbSimu = 0;

    public SimulationId(){
        id = nbSimu;
        nbSimu++;
    }

    public SimulationId(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    /*public static int getNumberofId(){
        return nbSimu;
    }*/

    @Override
    public String toString(){
        return "" + id;
    }

    @Override
    public boolean equals(Object o) {
        
        if (this == o) return true;
        
        if (o == null || getClass() != o.getClass()) return false;
        
        SimulationId that = (SimulationId) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
