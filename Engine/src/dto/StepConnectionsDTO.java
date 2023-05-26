package dto;

import javafx.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StepConnectionsDTO {

    Map<String, Map<String, String>> outputsConnections;
    Map<String, Pair<String, String>> inputsConnections;
    Map<String, Boolean> isMandatory;

    public StepConnectionsDTO(Map<String, Map<String, String>> outputsConnections, Map<String, Pair<String, String>> inputsConnections, Map<String, Boolean> isMandatory) {
        this.outputsConnections = outputsConnections;
        this.inputsConnections = inputsConnections;
        this.isMandatory = isMandatory;
    }

    public Map<String, Map<String, String>> getOutputsConnections() {
        return outputsConnections;
    }

    public Map<String, Pair<String, String>> getInputsConnections() {
        return inputsConnections;
    }

    public Map<String, Boolean> getIsMandatory() {
        return isMandatory;
    }
}
