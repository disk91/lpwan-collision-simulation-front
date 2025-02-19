package com.projetzz2.lpwan_colision_simulation.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import com.projetzz2.lpwan_colision_simulation.ServerLog;
import com.projetzz2.lpwan_colision_simulation.Simulation.SimulationConnecter;
import com.projetzz2.lpwan_colision_simulation.Simulation.SimulationId;
import com.projetzz2.lpwan_colision_simulation.Simulation.SimulationInput;
import com.projetzz2.lpwan_colision_simulation.Simulation.lpwan_collision_simulation.src.FrameModel;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class SimulationServerController {
    
    private Map<SimulationId, SimulationConnecter> allSimulations = new HashMap<>();

    private boolean checkMapId(SimulationId id){
        boolean idChecked = allSimulations.containsKey(id);

        if(!idChecked){
            throw new SimulationNotFoundException(id);
        }

        return idChecked;
    }

    @Operation(summary = "Create a new simulation with the informations put in input and return the id of the simulation",
            description = "Create the simulation and put it inside a map with an id that is given in the output",
            responses = {
                @ApiResponse(responseCode = "200", description = "Creating simulation", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SimulationId.class)))})
    @PostMapping("/new")
    public SimulationId createSimulation(
        @Parameter(required = true, name = "simuInput", description = "Parameters of the simulation")
        @RequestBody SimulationInput simuInput){
        
        SimulationId id = new SimulationId();
        ServerLog.printLog("Creating simulation: " + id.getId());
        
        allSimulations.put(id, new SimulationConnecter(simuInput));
        System.out.println(simuInput);

        return id;
    }

    @Operation(summary = "Get the values of a simulation",
            description = "Give the values of the simulation that has the id put in the path",
            responses = {
                @ApiResponse(responseCode = "200", description = "Getting simulation values", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SimulationConnecter.class))),
                @ApiResponse(responseCode = "404", description = "id not found", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"Unknown id\": 150}")))})
    @GetMapping("/get_values/{id}")
    public SimulationConnecter getSimulationStatues(
        @Parameter(required = true, name = "id", description = "Id of the simulation")
        @PathVariable SimulationId id){
        
        ServerLog.printLog("Getting values of simulation: " + id);
        SimulationConnecter simu = null;
        if(checkMapId(id)){
            simu = allSimulations.get(id);
            System.out.println(simu.getInput());
        }

        return simu;
    }

    @Operation(summary = "Change the input of the of the simulation and replace it by the one given in the body",
            description = "Take the new input of the simulation in the body and put it instead of the old values of the simulation",
            responses = {
                @ApiResponse(responseCode = "200", description = "Setting simulation parameters", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SimulationConnecter.class))),
                @ApiResponse(responseCode = "404", description = "id not found", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"Unknown id\": 150}")))})
    @PostMapping("/set_parameters/{id}")
    public SimulationConnecter changeParameters(
        @Parameter(required = true, name = "id", description = "Id of the simulation")
        @PathVariable SimulationId id,
        @Parameter(required = true, name = "simuInput", description = "Parameters of the simulation")
        @RequestBody SimulationInput simuInput){

        ServerLog.printLog("Changing parameters of simulation: " + id);
        System.out.println(simuInput);

        SimulationConnecter curSimu = null;

        if(checkMapId(id)){
            curSimu = allSimulations.get(id);
            curSimu.freeSimulation();
            curSimu.setLoRaWanFrames(new ArrayList<FrameModel>());
            curSimu.setMiotyFrames(new ArrayList<FrameModel>());
            curSimu.setSigfoxFrames(new ArrayList<FrameModel>());
            curSimu.setInput(simuInput);
        }

        return curSimu;
    }

    @Operation(summary = "Delete the simulation of the id in the path",
            description = "Free the simulation that has the same id as the one in the path and remove it from the map of all the simulations",
            responses = {
                @ApiResponse(responseCode = "200", description = "Deleting simulation", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SimulationId.class))),
                @ApiResponse(responseCode = "404", description = "id not found", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"Unknown id\": 150}")))})
    @PostMapping("/delete/{id}")
    public SimulationId removeSimulation(
        @Parameter(required = true, name = "id", description = "Id of the simulation")
        @PathVariable SimulationId id){

        ServerLog.printLog("Deleting simulation: " + id);

        if(checkMapId(id)){
            allSimulations.get(id).freeSimulation();
            allSimulations.remove(id);
        }

        return id;
    }


    @Operation(summary = "Create a new simulation with the informations put in input and return the id of the simulation",
            description = "Create the simulation and put it inside a map with an id that is given in the output",
            responses = {
                @ApiResponse(responseCode = "200", description = "Running simulation", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SimulationId.class))),
                @ApiResponse(responseCode = "404", description = "id not found", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SimulationNotFoundException.class),
                    examples = @ExampleObject(value = "{\"Unknown id\": 150}")))})
    @PostMapping("/run/{id}")
    public SimulationId runSimulation(
        @Parameter(required = true, name = "id", description = "Id of the simulation")
        @PathVariable SimulationId id){
        
        ServerLog.printLog("Running simulation: " + id);

        if(checkMapId(id)){
            allSimulations.get(id).simulationRun();
        }
        
        return id;
    }

    @Operation(summary = "Return all the id that are currently available",
            description = "Give all of the id that currently present inside of the map of all the simulations",
            responses = 
                @ApiResponse(responseCode = "200", description = "All id", content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = SimulationId.class)))))
    @GetMapping("/get_ids")
    public SimulationId[] getAllSimulations(){
        ServerLog.printLog("Request all id from server");

        return allSimulations.keySet().toArray(new SimulationId[0]);
    }

    @Operation(summary = "Pring the server",
            description = "Ping the server",
            responses = 
                @ApiResponse(responseCode = "200", description = "Ping", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Void.class),
                    examples = @ExampleObject(value = "{}"))))
    @GetMapping("/ping")
    public String pingBack(){
        //ServerLog.printLog("Ping Server");
        return "{}";
    }
}
