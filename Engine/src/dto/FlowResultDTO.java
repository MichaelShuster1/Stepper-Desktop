package dto;

import java.util.List;

public class FlowResultDTO {
    private final String id;
    private final String name;
    private final String state;
    private final List<OutputExecutionDTO> formalOutputs;

    public FlowResultDTO(String id, String name, String state, List<OutputExecutionDTO> formalOutputs) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.formalOutputs = formalOutputs;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getState() {
        return state;
    }

    public List<OutputExecutionDTO> getFormalOutputs() {
        return formalOutputs;
    }
}
