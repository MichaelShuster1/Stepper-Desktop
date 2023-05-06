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

    private final Flow flowDefinition;

    public FlowExecution(Flow flowDefinition) {
        this.flowDefinition = flowDefinition;
        initSteps();
    }

    private void initSteps() {
        steps = new ArrayList<>();
        List<Step> definitionSteps = flowDefinition.getSteps();
        for(int i = 0; i< definitionSteps.size(); i++) {
            Step currStep = definitionSteps.get(i);
            Step newStep = HCSteps.CreateStep(currStep.getDefaultName(), currStep.getName(), currStep.isContinueIfFailing());
            List<Input> currStepInputs =  currStep.getInputs();
            for(int j = 0; j< currStepInputs.size(); j++) {
                newStep.getInput(j).setData((currStepInputs.get(j)).getData());
            }
            steps.add(currStep);
        }
    }

    @Override
    public void run() {

    }
}
