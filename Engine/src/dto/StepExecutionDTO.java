package dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StepExecutionDTO implements Serializable {
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
        this.logs=new ArrayList<>();
        this.logs.addAll(logs);
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
