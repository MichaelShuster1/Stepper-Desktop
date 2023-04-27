package dto;

import java.util.Set;

public class FlowDetailsDTO {

    private final String name;
    private final String description;
    private final Set<String> formal_outputs;
    private final boolean readOnly;

    public FlowDetailsDTO(String name, String description, Set<String> formal_outputs, boolean readOnly) {
        this.name = name;
        this.description = description;
        this.formal_outputs = formal_outputs;
        this.readOnly = readOnly;
    }

    public FlowDetailsDTO(FlowDetailsDTO other) {
        this.name = other.name;
        this.description = other.description;
        this.formal_outputs = other.formal_outputs;
        this.readOnly = other.readOnly;
    }


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Set<String> getFormal_outputs() {
        return formal_outputs;
    }

    public boolean isReadOnly() {
        return readOnly;
    }
}
