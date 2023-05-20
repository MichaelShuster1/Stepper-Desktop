package dto;

import java.io.Serializable;

public class FlowExecutionDetailsDTO implements Serializable {
    private final String name;
    private final String id;
    private final String stateAfterRun;
    private final String activationTime;
    private final long runTime;

    public FlowExecutionDetailsDTO(String name, String id, String stateAfterRun, String activationTime, long runTime) {
        this.name = name;
        this.id = id;
        this.stateAfterRun = stateAfterRun;
        this.runTime = runTime;
        this.activationTime = activationTime;
    }

    public FlowExecutionDetailsDTO(FlowExecutionDetailsDTO other)
    {
        this.name = other.name;
        this.id = other.id;
        this.stateAfterRun = other.stateAfterRun;
        this.runTime = other.runTime;
        this.activationTime =other.activationTime;
    }


    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getStateAfterRun() {
        return stateAfterRun;
    }

    public long getRunTime() {
        return runTime;
    }

    public String getActivationTime()
    {
        return activationTime;
    }


}
