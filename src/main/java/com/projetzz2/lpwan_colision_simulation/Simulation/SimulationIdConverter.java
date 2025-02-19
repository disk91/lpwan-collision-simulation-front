package com.projetzz2.lpwan_colision_simulation.Simulation;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class SimulationIdConverter implements Converter<String, SimulationId>{
    @Override
    public SimulationId convert(@NonNull String source) {
        try {
            int id = Integer.parseInt(source);
            return new SimulationId(id);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Valeur de SimulationId invalide: " + source, e);
        }
    }
}
