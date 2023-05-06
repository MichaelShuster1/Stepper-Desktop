package flow;

import step.State;
import step.Step;

import java.util.List;

public class FlowExecution implements Runnable{
    private String flowId;
    private State stateAfterRun;
    private Long runTime;
    private String activationTime;
    private List<Step> steps;

    @Override
    public void run() {

    }
}
