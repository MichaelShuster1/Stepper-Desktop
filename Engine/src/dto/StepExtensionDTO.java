package dto;

import java.util.List;
import java.util.Map;

public class StepExtensionDTO {
    List<String> logs;
    private Map<String,Object> inputs;

    private Map<String,Object> outputs;


    public StepExtensionDTO(List<String> logs,Map<String,Object> inputs,Map<String,Object> outputs) {
        this.logs=logs;
        this.inputs=inputs;
        this.outputs=outputs;
    }

    public Map<String, Object> getInputs() {
        return inputs;
    }

    public Map<String, Object> getOutputs() {
        return outputs;
    }

    public List<String> getLogs() {
        return logs;
    }
}
