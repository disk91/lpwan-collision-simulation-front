package com.projetzz2.lpwan_colision_simulation.models;

import com.projetzz2.lpwan_colision_simulation.models.enums.*;

public class SimulationServeurAPI {

	private ServerActions actions;
    private SimulationServerAPIParameters parameters;
    private String response;
    private ServeurStatues status;
    private SimulationServeurAPIValues values;

    public SimulationServeurAPI(){
        actions = null;
        parameters = null;
        response = null;
        status = null;
        values = null;
    }

    public ServerActions getActions() {
        return actions;
    }
    public void setActions(ServerActions actions) {
        this.actions = actions;
    }
    public SimulationServerAPIParameters getParameters() {
        return parameters;
    }
    public void setParameters(SimulationServerAPIParameters parameters) {
        this.parameters = parameters;
    }
    public String getResponse() {
        return response;
    }
    public void setResponse(String response) {
        this.response = response;
    }
    public ServeurStatues getStatus() {
        return status;
    }
    public void setStatus(ServeurStatues status) {
        this.status = status;
    }
    public SimulationServeurAPIValues getValues() {
        return values;
    }
    public void setValues(SimulationServeurAPIValues values) {
        this.values = values;
    }

}
