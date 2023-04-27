package dto;

import java.util.List;

public class StepExecutionDTO {
    String name;
    long runTime;
    String stateAfterRun;
    String summaryLine;
    List<String> logs;

    public StepExecutionDTO(String name, long runTime, String stateAfterRun, String summaryLine, List<String> logs) {
        this.name = name;
        this.runTime = runTime;
        this.stateAfterRun = stateAfterRun;
        this.summaryLine = summaryLine;
        this.logs = logs;
    }

    public String getName() {
        return name;
    }

    public long getRunTime() {
        return runTime;
    }

    public String getStateAfterRun() {
        return stateAfterRun;
    }

    public String getSummaryLine() {
        return summaryLine;
    }

    public List<String> getLogs() {
        return logs;
    }
}
