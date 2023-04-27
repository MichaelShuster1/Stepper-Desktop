package dto;

public class StepDefinitionDTO {
    private final String name;
    private final String defaultName;
    private final boolean readOnly;

    public StepDefinitionDTO(String name,String defaultName, boolean readOnly) {
        this.name = name;
        this.readOnly = readOnly;
        this.defaultName = defaultName;

    }

    public String getName() {
        return name;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public String getDefaultName() {
        return defaultName;
    }
}
