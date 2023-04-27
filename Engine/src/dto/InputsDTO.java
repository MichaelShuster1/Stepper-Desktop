package dto;

import java.util.List;

public class InputsDTO {
    private final List<InputData> freeInputs;

    private final Integer numberOfInputs;

    private final String flowName;

    public InputsDTO(List<InputData> freeInputs, String flowName) {
        this.freeInputs = freeInputs;
        this.flowName = flowName;
        this.numberOfInputs = freeInputs.size();
    }

    public InputData getFreeInput(int index) {
        return freeInputs.get(index);
    }

    public String getFlowName() {
        return flowName;
    }

    public Integer getNumberOfInputs() {
        return numberOfInputs;
    }
}
