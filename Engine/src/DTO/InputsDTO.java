package DTO;

import java.util.List;

public class InputsDTO
{
    private List<InputData> freeInputs;

    private Integer numberOfInputs;

    private String flowName;

    public InputsDTO(List<InputData> freeInputs, String flowName)
    {
        this.freeInputs = freeInputs;
        this.flowName =flowName;
        this.numberOfInputs=freeInputs.size();
    }

    public InputData getFreeInput(int index)
    {
        return freeInputs.get(index);
    }

    public String getFlowName()
    {
        return flowName;
    }

    public Integer getNumberOfInputs()
    {
        return numberOfInputs;
    }
}
