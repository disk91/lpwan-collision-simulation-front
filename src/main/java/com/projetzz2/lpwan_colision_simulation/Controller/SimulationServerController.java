package com.projetzz2.lpwan_colision_simulation.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.*;

import com.projetzz2.lpwan_colision_simulation.Simulation.SimulationConnecter;
import com.projetzz2.lpwan_colision_simulation.Simulation.lpwan_collision_simulation.src.FrameModel;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class SimulationServerController {
    
    private Map<Integer, SimulationConnecter> allSimulations = new HashMap<>();
    private int lastId = 0;
    private LocalDateTime dateHeureActuelle = LocalDateTime.now();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private void printLogTime(){
        dateHeureActuelle = LocalDateTime.now();
        System.out.print("[" + dateHeureActuelle.format(formatter) + "]");
    }

    @PostMapping("/new")
    public String createSimulation(@RequestBody SimulationConnecter simu){

        System.out.println("Nombre de msg/s: " + simu.getSimulationMessagePerSecond() + ", LoRaWan Run: " + simu.isLoRaWanRun()+ ", Mioty Run: " + simu.isMiotyModelRun() + ", Sigfox Run: " + simu.isSigfoxModelRun());
        printLogTime();
        System.out.println("  -  Creating simulation " + lastId);

        simu.setSimulationRunning(false);
        simu.setLoRaWanFrames(new ArrayList<FrameModel>());
        simu.setMiotyFrames(new ArrayList<FrameModel>());
        simu.setSigfoxFrames(new ArrayList<FrameModel>());
        allSimulations.put(lastId, simu);
        lastId++;

        return "{\n  \"id\": " + (lastId-1) + "\n}";
    }

    @GetMapping("/get_values/{id}")
    public SimulationConnecter getSimulationStatues(@PathVariable int id){
        printLogTime();
        System.out.println("  -  Getting values of simulation " + id);

        return allSimulations.get(id);
    }

    @PostMapping("/set_parameters/{id}")
    public SimulationConnecter changeParameters(@PathVariable int id, @RequestBody SimulationConnecter simu){
        System.out.println("Nombre de msg/s: " + simu.getSimulationMessagePerSecond() + ", LoRaWan Run: " + simu.isLoRaWanRun()+ ", Mioty Run: " + simu.isMiotyModelRun() + ", Sigfox Run: " + simu.isSigfoxModelRun());
        printLogTime();
        System.out.println("  -  Changing parameters of simulation " + id);

        SimulationConnecter curSimu = allSimulations.get(id);
        curSimu.freeSimulation();

        curSimu.setSimulationRunning(false);
        curSimu.setLoRaWanFrames(new ArrayList<FrameModel>());
        curSimu.setMiotyFrames(new ArrayList<FrameModel>());
        curSimu.setSigfoxFrames(new ArrayList<FrameModel>());
        curSimu.setLoRaWanRun(simu.isLoRaWanRun());
        curSimu.setMiotyModelRun(simu.isMiotyModelRun());
        curSimu.setSigfoxModelRun(simu.isSigfoxModelRun());
        curSimu.setSimulationMessagePerSecond(simu.getSimulationMessagePerSecond());

        return curSimu;
    }

    @PostMapping("/delete/{id}")
    public String removeSimulation(@PathVariable int id){
        printLogTime();
        System.out.println("  -  Deleting simulation " + id);

        allSimulations.get(id).freeSimulation();
        allSimulations.remove(id);

        return "{\n  \"Running deleted\": " + id + "\n}";
    }



    @PostMapping("/run/{id}")
    public String runSimulation(@PathVariable int id){
        printLogTime();
        System.out.println("  -  Running simulation " + id);

        allSimulations.get(id).simulationRun();
        return "{\n  \"Running simulation\": " + id + "\n}";
    }

    @GetMapping("/get_ids")
    public String getAllSimulations(){
        printLogTime();
        System.out.println("  -  Request all id from server");

        String out = "{\n  \"id\": [";
        int i=0, mapSize = allSimulations.size();
        for (Integer key : allSimulations.keySet()) {
            out +=  key;
            if(i < mapSize-1){
                out += ", ";
            }

            i++;
        }

        out += "]\n}";
        return out;
    }

    @GetMapping("/ping")
    public String pingBack(){
        printLogTime();
        System.out.println("  -  Ping Server");
        return "{}";
    }
}
