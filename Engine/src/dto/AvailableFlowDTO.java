package dto;

public class AvailableFlowDTO {
    private String name;
    private String description;
    private int numberOfInputs;

    private int numberOfSteps;

    private int numberOfContinuations;

    public AvailableFlowDTO(String name, String description, int numberOfInputs, int numberOfSteps, int numberOfContinuations) {
        this.name = name;
        this.description = description;
        this.numberOfInputs = numberOfInputs;
        this.numberOfSteps = numberOfSteps;
        this.numberOfContinuations = numberOfContinuations;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getNumberOfInputs() {
        return numberOfInputs;
    }

    public int getNumberOfSteps() {
        return numberOfSteps;
    }

    public int getNumberOfContinuations() {
        return numberOfContinuations;
    }
}
