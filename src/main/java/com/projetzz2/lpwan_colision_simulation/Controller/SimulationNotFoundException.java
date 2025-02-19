package com.projetzz2.lpwan_colision_simulation.Controller;

import com.projetzz2.lpwan_colision_simulation.Simulation.SimulationId;

public class SimulationNotFoundException extends RuntimeException {

    private SimulationId id;

    public SimulationNotFoundException(SimulationId id) {
        super("{\"Unknown id\": " + id +"}");
        this.id = id;
    }

    public SimulationId getSimulationId(){
        return id;
    }
}
