package dto;

public class StepDefinitionDTO {
    private final String name;
    private final String defaultName;
    private final boolean readOnly;

    private StepConnectionsDTO connections;

    public StepDefinitionDTO(String name, String defaultName, boolean readOnly, StepConnectionsDTO connections) {
        this.name = name;
        this.defaultName = defaultName;
        this.readOnly = readOnly;
        this.connections = connections;
    }

    public StepDefinitionDTO(String name, String defaultName, boolean readOnly) {
        this.name = name;
        this.readOnly = readOnly;
        this.defaultName = defaultName;


    }

    public StepConnectionsDTO getConnections() {
        return connections;
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
