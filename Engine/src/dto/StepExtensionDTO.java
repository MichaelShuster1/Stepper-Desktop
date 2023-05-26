package dto;

import java.util.List;
import java.util.Map;

public class StepExtensionDTO {
    List<String> logs;
    private Map<DataDefintionDTO,Object> inputs;

    private Map<DataDefintionDTO,Object> outputs;


    public StepExtensionDTO(List<String> logs,Map<DataDefintionDTO,Object> inputs,Map<DataDefintionDTO,Object> outputs) {
        this.logs=logs;
        this.inputs=inputs;
        this.outputs=outputs;
    }

    public Map<DataDefintionDTO, Object> getInputs() {
        return inputs;
    }

    public Map<DataDefintionDTO, Object> getOutputs() {
        return outputs;
    }

    public List<String> getLogs() {
        return logs;
    }
}
