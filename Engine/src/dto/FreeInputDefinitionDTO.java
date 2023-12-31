package dto;

import java.util.List;

public class FreeInputDefinitionDTO extends  DataDefintionDTO
{
    private final boolean necessity;
    private final List<String> relatedSteps;

    public FreeInputDefinitionDTO(DataDefintionDTO inputData, List<String> relatedSteps, boolean necessity) {
        super(inputData);
        this.relatedSteps = relatedSteps;
        this.necessity = necessity;
    }


    public List<String> getRelatedSteps() {
        return relatedSteps;
    }

    public boolean isMandatory() {
        return necessity;
    }
}
