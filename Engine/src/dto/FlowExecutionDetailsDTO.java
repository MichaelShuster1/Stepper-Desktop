package dto;

import java.io.Serializable;

public class FlowExecutionDetailsDTO implements Serializable {
    private final String name;
    private final String id;
    private final String stateAfterRun;
    private final long runTime;

    public FlowExecutionDetailsDTO(String name, String id, String stateAfterRun, long runTime) {
        this.name = name;
        this.id = id;
        this.stateAfterRun = stateAfterRun;
        this.runTime = runTime;
    }

    public FlowExecutionDetailsDTO(FlowExecutionDetailsDTO other)
    {
        this.name = other.name;
        this.id = other.id;
        this.stateAfterRun = other.stateAfterRun;
        this.runTime = other.runTime;
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
}
