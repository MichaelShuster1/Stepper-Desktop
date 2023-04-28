package dto;

import java.io.Serializable;
import java.util.List;

public class FlowExecutionDTO extends FlowExecutionDetailsDTO{
    private final List<StepExecutionDTO> steps;
    private final List<FreeInputExecutionDTO> freeInputs;
    private final List<OutputExecutionDTO> outputs;

    public FlowExecutionDTO(FlowExecutionDetailsDTO other, List<StepExecutionDTO> steps, List<FreeInputExecutionDTO> freeInputs, List<OutputExecutionDTO> outputs) {
        super(other);
        this.steps = steps;
        this.freeInputs = freeInputs;
        this.outputs = outputs;
    }

    public List<StepExecutionDTO> getSteps() {
        return steps;
    }

    public List<FreeInputExecutionDTO> getFreeInputs() {
        return freeInputs;
    }

    public List<OutputExecutionDTO> getOutputs() {
        return outputs;
    }
}
