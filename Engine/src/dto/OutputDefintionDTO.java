package dto;

import java.util.Map;

public class OutputDefintionDTO extends DataDefintionDTO
{
    private final String stepName;

    private Map<String,String> connections;

    public OutputDefintionDTO(DataDefintionDTO dataDefintionDTO, String stepName) {
        super(dataDefintionDTO);
        this.stepName = stepName;
    }


    public String getStepName() {
        return stepName;
    }
}
