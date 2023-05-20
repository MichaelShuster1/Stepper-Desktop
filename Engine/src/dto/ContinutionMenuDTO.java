package dto;

import java.util.List;

public class ContinutionMenuDTO {
    private List<String> targetFlows;

    public ContinutionMenuDTO(List<String> targetFlows) {
        this.targetFlows = targetFlows;
    }

    public List<String> getTargetFlows() {
        return targetFlows;
    }
}
