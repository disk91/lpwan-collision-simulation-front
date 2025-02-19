package com.projetzz2.lpwan_colision_simulation.Controller;

public class SimulationNotFoundException extends RuntimeException {

    public SimulationNotFoundException(String message) {
        super(message);
    }

    // Tu peux aussi ajouter d'autres constructeurs si nécessaire, par exemple :
    public SimulationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
