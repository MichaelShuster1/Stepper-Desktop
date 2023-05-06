package flow;

import datadefinition.Input;
import datadefinition.Output;
import dto.*;
import hardcodeddata.HCSteps;
import javafx.util.Pair;
import step.CollectFiles;
import step.State;
import step.Step;

import java.text.SimpleDateFormat;
import java.util.*;

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
            steps.add(newStep);
        }
    }

    @Override
    public void run() {
        Long startTime = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.format(new Date());
        activationTime = formatter.format(new Date());
        boolean continueExecution = true;
        stateAfterRun = State.SUCCESS;

        for (int i = 0; i < steps.size() && continueExecution; i++) {
            Step currStep = steps.get(i);
            currStep.run();

            if (currStep.getStateAfterRun() == State.FAILURE) {
                if (!currStep.isContinueIfFailing()) {
                    stateAfterRun = State.FAILURE;
                    continueExecution = false;
                }
                else
                    stateAfterRun =State.WARNING;
            }

            if (continueExecution) {
                if (currStep.getStateAfterRun() == State.WARNING)
                    stateAfterRun = State.WARNING;
                streamStepOutputsToInputs(i, currStep);
            }
        }

        flowId = generateFlowId();
        runTime = System.currentTimeMillis() - startTime;
    }


    private void streamStepOutputsToInputs(int i, Step currStep) {
        List<List<Pair<Integer, Integer>>> stepConnections = flowDefinition.getStepConnections(i);
        int outPutIndex=0;
        for (List<Pair<Integer, Integer>> currOutput : stepConnections) {
            for (Pair<Integer, Integer> currConnection : currOutput) {
                int targetStepIndex = currConnection.getKey();
                int targetStepInputIndex = currConnection.getValue();
                steps.get(targetStepIndex).getInput(targetStepInputIndex).setData(currStep.getOutput(outPutIndex).getData());

            }
            outPutIndex++;
        }
    }

    private String generateFlowId() {
        UUID uuid = UUID.randomUUID();
        String uuidAsString = uuid.toString();
        return uuidAsString;
    }

    public FlowResultDTO getFlowExecutionResultData()
    {
        List<OutputExecutionDTO> formalOutputs = new ArrayList<>();
        Map<String,Integer> outputs =flowDefinition.getFormalOutputs();
        for (String currOutput : outputs.keySet()) {
            Step step = steps.get(outputs.get(currOutput));
            int outPutIndex = step.getNameToOutputIndex().get(currOutput);
            String userString = "    " + step.getOutput(outPutIndex).getUserString();
            DataDefintionDTO currDetails = new DataDefintionDTO(null, userString);
            OutputExecutionDTO currDTO;
            if (step.getOutput(outPutIndex).getData() != null)
                currDTO = new OutputExecutionDTO(currDetails, step.getOutput(outPutIndex).getData().toString());
            else
                currDTO = new OutputExecutionDTO(currDetails, null);
            formalOutputs.add(currDTO);
        }

        return new FlowResultDTO(flowId,flowDefinition.getName(), stateAfterRun.toString(),formalOutputs);
    }


    public FlowExecutionDTO getFlowHistoryData()
    {
        FlowExecutionDetailsDTO executionDetails = new FlowExecutionDetailsDTO(flowDefinition.getName(),flowId, stateAfterRun.toString(),runTime);
        List<StepExecutionDTO> steps = getStepsExecutionDTO();
        List<FreeInputExecutionDTO> freeInputs = getFreeInputsExecutionDTO();
        List<OutputExecutionDTO> outputs = getOutputsExecutionDTO();
        return new FlowExecutionDTO(executionDetails,steps,freeInputs,outputs);
    }


    private List<OutputExecutionDTO> getOutputsExecutionDTO() {
        List<OutputExecutionDTO> outputsList = new ArrayList<>();
        for (int i = 0; i < steps.size(); i++) {
            Step step = steps.get(i);
            List<Output> outputs = step.getOutputs();
            for (Output output : outputs) {
                DataDefintionDTO outputDetails = new DataDefintionDTO(output.getName(),output.getType());
                if (output.getData() != null)
                    outputsList.add(new OutputExecutionDTO(outputDetails,output.getDataDefinition().toString()));
                else
                    outputsList.add(new OutputExecutionDTO(outputDetails,null));
            }
        }
        return outputsList;
    }


    private List<FreeInputExecutionDTO> getFreeInputsExecutionDTO() {
        List<FreeInputExecutionDTO> inputsList = new ArrayList<>();
        Map<String,List<Integer>> flowFreeInputs= flowDefinition.getFlowFreeInputs();
        for (String key : flowFreeInputs.keySet()) {
            List<Integer> inputs = flowFreeInputs.get(key);
            int i = inputs.get(0);
            int inputIndex = steps.get(i).getNameToInputIndex().get(key);
            Input input = steps.get(i).getInput(inputIndex);
            DataDefintionDTO inputDetails = new DataDefintionDTO(input.getName(),input.getType());
            if (input.getData() != null)
                inputsList.add(new FreeInputExecutionDTO(inputDetails,input.getData().toString(),input.isMandatory()));
            else
                inputsList.add(new FreeInputExecutionDTO(inputDetails,null,input.isMandatory()));
        }

        return inputsList;
    }


    private List<StepExecutionDTO> getStepsExecutionDTO() {
        List<StepExecutionDTO> stepsList = new ArrayList<>();
        boolean flowStopped = false;
        for (int i = 0; i < steps.size() && !flowStopped; i++) {
            Step step = steps.get(i);
            stepsList.add(step.getStepExecutionData());
            if (step.getStateAfterRun() == State.FAILURE && !step.isContinueIfFailing())
                flowStopped = true;
        }
        return stepsList;
    }

    public String getFlowId() {
        return flowId;
    }

    public State getStateAfterRun() {
        return stateAfterRun;
    }

    public Long getRunTime() {
        return runTime;
    }

    public String getActivationTime() {
        return activationTime;
    }

    public String getName() {
        return flowDefinition.getName();
    }

    public int getNumberOfSteps() {
        return steps.size();
    }

    public Step getStep(int i){
        return steps.get(i);
    }

}
