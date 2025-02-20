/**
 * SimulationServerController.java
 *
 * This class is a Spring REST controller that manages simulation sessions.
 * It exposes API endpoints to create, retrieve, update, run, and delete simulations.
 * Simulations are stored in a map with their corresponding SimulationId as the key.
 * The controller uses Swagger annotations for API documentation and allows 
 * cross-origin requests from "http://localhost:3000".
 */

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
     
     // Map to store all active simulations, keyed by their SimulationId.
     private Map<SimulationId, SimulationConnecter> allSimulations = new HashMap<>();
 
     /**
      * Checks if a given SimulationId exists in the simulation map.
      * Throws a SimulationNotFoundException if the id does not exist.
      *
      * @param id The SimulationId to check.
      * @return true if the SimulationId exists in the map.
      */
     private boolean checkMapId(SimulationId id) {
         boolean idChecked = allSimulations.containsKey(id);
 
         if (!idChecked) {
             throw new SimulationNotFoundException(id);
         }
 
         return idChecked;
     }
 
     /**
      * Creates a new simulation using the provided input parameters.
      * A new SimulationId is generated and the simulation is stored in the map.
      *
      * @param simuInput The input parameters for the simulation.
      * @return The generated SimulationId for the new simulation.
      */
     @Operation(summary = "Create a new simulation with the informations put in input and return the id of the simulation",
             description = "Create the simulation and put it inside a map with an id that is given in the output",
             responses = {
                 @ApiResponse(responseCode = "200", description = "Creating simulation", content = @Content(
                     mediaType = "application/json",
                     schema = @Schema(implementation = SimulationId.class)))})
     @PostMapping("/new")
     public SimulationId createSimulation(
         @Parameter(required = true, name = "simuInput", description = "Parameters of the simulation")
         @RequestBody SimulationInput simuInput) {
         
         // Generate a new SimulationId
         SimulationId id = new SimulationId();
         // Log the creation of the simulation
         ServerLog.printLog("Creating simulation: " + id.getId());
         
         // Create a new SimulationConnecter with the provided input and store it in the map
         allSimulations.put(id, new SimulationConnecter(simuInput));
         System.out.println(simuInput);
 
         return id;
     }
 
     /**
      * Retrieves the current state (values) of a simulation identified by the given SimulationId.
      *
      * @param id The SimulationId of the simulation to retrieve.
      * @return The SimulationConnecter object representing the simulation's state.
      */
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
         @PathVariable SimulationId id) {
         
         // Log the retrieval of simulation values
         ServerLog.printLog("Getting values of simulation: " + id);
         SimulationConnecter simu = null;
         
         // Check if the simulation exists and retrieve it
         if (checkMapId(id)) {
             simu = allSimulations.get(id);
             System.out.println(simu.getInput());
         }
 
         return simu;
     }
 
     /**
      * Updates the parameters of an existing simulation.
      * Clears the simulation's frame data before applying the new parameters.
      *
      * @param id The SimulationId of the simulation to update.
      * @param simuInput The new simulation parameters.
      * @return The updated SimulationConnecter object.
      */
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
         @RequestBody SimulationInput simuInput) {
 
         // Log the parameter change request
         ServerLog.printLog("Changing parameters of simulation: " + id);
         System.out.println(simuInput);
 
         SimulationConnecter curSimu = null;
 
         // Check if the simulation exists; if so, clear its current data and update its parameters.
         if (checkMapId(id)) {
             curSimu = allSimulations.get(id);
             curSimu.freeSimulation();
             curSimu.setLoRaWanFrames(new ArrayList<FrameModel>());
             curSimu.setMiotyFrames(new ArrayList<FrameModel>());
             curSimu.setSigfoxFrames(new ArrayList<FrameModel>());
             curSimu.setInput(simuInput);
         }
 
         return curSimu;
     }
 
     /**
      * Deletes an existing simulation.
      * Frees the simulation's resources and removes it from the map.
      *
      * @param id The SimulationId of the simulation to delete.
      * @return The SimulationId of the deleted simulation.
      */
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
         @PathVariable SimulationId id) {
 
         // Log the deletion of the simulation
         ServerLog.printLog("Deleting simulation: " + id);
 
         // Check if the simulation exists; if so, free its resources and remove it from the map.
         if (checkMapId(id)) {
             allSimulations.get(id).freeSimulation();
             allSimulations.remove(id);
         }
 
         return id;
     }
 
     /**
      * Runs a simulation.
      *
      * @param id The SimulationId of the simulation to run.
      * @return The SimulationId of the simulation that has been started.
      */
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
         @PathVariable SimulationId id) {
         
         // Log the simulation run request
         ServerLog.printLog("Running simulation: " + id);
 
         // Check if the simulation exists; if so, start the simulation.
         if (checkMapId(id)) {
             allSimulations.get(id).simulationRun();
         }
         
         return id;
     }
 
     /**
      * Retrieves all currently active simulation IDs.
      *
      * @return An array of SimulationId representing all active simulations.
      */
     @Operation(summary = "Return all the id that are currently available",
             description = "Give all of the id that currently present inside of the map of all the simulations",
             responses = 
                 @ApiResponse(responseCode = "200", description = "All id", content = @Content(
                     mediaType = "application/json",
                     array = @ArraySchema(schema = @Schema(implementation = SimulationId.class)))))
     @GetMapping("/get_ids")
     public SimulationId[] getAllSimulations() {
         // Log the request for all simulation IDs
         ServerLog.printLog("Request all id from server");
 
         // Convert the key set of the simulation map to an array and return it.
         return allSimulations.keySet().toArray(new SimulationId[0]);
     }
 
     /**
      * Pings the server.
      *
      * @return A simple empty JSON object as a string.
      */
     @Operation(summary = "Pring the server",
             description = "Ping the server",
             responses = 
                 @ApiResponse(responseCode = "200", description = "Ping", content = @Content(
                     mediaType = "application/json",
                     schema = @Schema(implementation = Void.class),
                     examples = @ExampleObject(value = "{}"))))
     @GetMapping("/ping")
     public String pingBack() {
         // Optionally log the ping event (currently commented out)
         // ServerLog.printLog("Ping Server");
         return "{}";
     }
 }
 