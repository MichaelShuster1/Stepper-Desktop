package dto;

public class OutputDefintionDTO extends DataDefintionDTO
{
    String stepName;

    public OutputDefintionDTO(DataDefintionDTO dataDefintionDTO, String stepName) {
        super(dataDefintionDTO);
        this.stepName = stepName;
    }


    public String getStepName() {
        return stepName;
    }
}
