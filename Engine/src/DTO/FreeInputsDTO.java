package DTO;

import java.util.List;

public class FreeInputsDTO
{
    private List<FreeInputData> freeInputs;

    private Integer numberOfInputs;

    private String flowName;

    public FreeInputsDTO(List<FreeInputData> freeInputs,String flowName)
    {
        this.freeInputs = freeInputs;
        this.flowName =flowName;
        this.numberOfInputs=freeInputs.size();
    }

    public FreeInputData getFreeInput(int index)
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
