package com.projetzz2.lpwan_colision_simulation.Controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projetzz2.lpwan_colision_simulation.Simulation.SimulationConnecter;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class SimulationServerController {
    
    private Map<Integer, SimulationConnecter> allSimulations = new HashMap<>();
    private int lastId = 0;

    @GetMapping("/values/{id}")
    public SimulationConnecter getSimulationStatues(@PathVariable int id){
        return allSimulations.get(id);
    }

    @GetMapping("/run/{id}")
    public String runSimulation(@PathVariable int id){
        allSimulations.get(id).simulationRun();
        return "{\n  \"Running simulation\": " + id + "\n}";
    }

    @PostMapping("/new")
    public String createSimulation(){
        allSimulations.put(lastId, new SimulationConnecter());
        lastId++;

        return "{\n  \"id\": " + (lastId-1) + "\n}";
    }

}
