package dto;

import java.util.List;
import java.util.Set;

public class FlowDefinitionDTO extends FlowDetailsDTO{

    private final List<StepDefinitionDTO> steps;
    private final List<FreeInputDefinitionDTO> freeInputs;
    private final List<OutputDefintionDTO> outputs;

    public FlowDefinitionDTO(FlowDetailsDTO flowDetailsDTO, List<StepDefinitionDTO> steps, List<FreeInputDefinitionDTO> freeInputs, List<OutputDefintionDTO> outputs) {
        super(flowDetailsDTO);
        this.steps = steps;
        this.freeInputs = freeInputs;
        this.outputs = outputs;
    }

    public List<StepDefinitionDTO> getSteps() {
        return steps;
    }

    public List<FreeInputDefinitionDTO> getFreeInputs() {
        return freeInputs;
    }

    public List<OutputDefintionDTO> getOutputs() {
        return outputs;
    }
}


