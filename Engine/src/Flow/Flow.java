package Flow;

import DTO.InputData;
import DTO.InputsDTO;
import DTO.ResultDTO;
import DataDefinitions.Input;
import DataDefinitions.Output;
import Steps.State;
import Steps.Step;
import exceptions.*;
import javafx.util.Pair;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;


public class Flow implements Serializable {
    private final String name;
    private final String description;
    private boolean read_only;
    private String flowId;
    private State state_after_run;
    private Long runTime;
    private String activationTime;
    private Map<String, Integer> formal_outputs;
    private List<Step> steps;
    private int numberOfSteps;
    private Map<String, Integer> nameToIndex;
    private List<List<List<Pair<Integer, Integer>>>> connections;
    private Map<String, List<Integer>> flowInputs;
    private Map<String, List<Integer>> flowFreeInputs;
    private Map<String, Boolean> freeInputsIsReq;
    private Set<String> freeMandatoryInputs;

    public Flow(String name, String description) {
        this.name = name;
        this.description = description;
        steps = new ArrayList<>();
        nameToIndex = new HashMap<>();
        formal_outputs = new HashMap<>();
        numberOfSteps = 0;

    }

    public void AddStep(Step step) {
        steps.add(step);
        numberOfSteps++;
        nameToIndex.put(step.getName(), steps.size() - 1);
    }

    public Integer getNumberOfSteps() {
        return numberOfSteps;
    }

    public void addFormalOutput(String outputName) {
        formal_outputs.put(outputName, -1);
    }

    public void CustomMapping(Map<Pair<String, String>, Pair<String, String>> customMapping) {
        initConnections();

        for (Pair<String, String> key : customMapping.keySet()) {
            Pair<String, String> currValue = customMapping.get(key);
            Integer outPutStepIndex = nameToIndex.get(key.getKey());
            if(outPutStepIndex == null) {
                throw new StepNameNotExistException("The Custom mapping in the flow \"" + name +"\" contains mapping for a step that doesn't exist, step name:" + key.getKey());
            }
            Integer outPutIndex = steps.get(outPutStepIndex).getNameToOutputIndex().get(key.getValue());
            if(outPutIndex == null) {
                throw new InputOutputNotExistException("The Custom mapping in the flow \"" + name +"\" contains mapping for a step's input that doesn't exist, step name:"
                        + key.getKey() + " input name:" + key.getValue());
            }
            Integer inputStepIndex = nameToIndex.get(currValue.getKey());
            if(inputStepIndex == null) {
                throw new StepNameNotExistException("The Custom mapping in the flow \"" + name +"\" contains mapping for a step that doesn't exist, step name:" + currValue.getKey());
            }
            Integer inputIndex = steps.get(inputStepIndex).getNameToInputIndex().get(currValue.getValue());
            if(inputIndex == null) {
                throw new InputOutputNotExistException("The Custom mapping in the flow \"" + name +"\" contains mapping for a step's input that doesn't exist\n step name:"
                        + key.getKey() + " input name:" + currValue.getValue());
            }

            if(outPutStepIndex >= inputStepIndex) {
                throw new StepsMappingOrderException("The Custom mapping in the flow \"" + name +"\" contains mapping from the step:" + key.getValue() +" to the step:" + currValue.getKey() +
                        "While the step:" + currValue.getKey() + "is executed in the flow before the step:" + key.getValue());
            }

            if(!(steps.get(outPutStepIndex).getInput(outPutIndex).getType().equals(steps.get(inputStepIndex).getInput(inputIndex).getType()))) {
                throw new StepsMappingOrderException("The Custom mapping in the flow \"" + name +"\" contains mapping for the input:" + currValue.getValue() + "\nto the output:" + key.getValue() +
                        "while the input and output have data of different types");
            }

            connections.get(outPutStepIndex).get(outPutIndex).add(new Pair<>(inputStepIndex, inputIndex));
            steps.get(inputStepIndex).getInput(inputIndex).setConnected(true);
        }
    }


    public void AutomaticMapping() {
        int a;
        read_only = true;

        initFlowInputs();
        if (connections == null)
            initConnections();

        for (int i = 0; i < steps.size(); i++) {
            Step step = steps.get(i);
            if (!step.isRead_only())
                read_only = false;
            List<Output> outputs = step.getOutputs();
            List<List<Pair<Integer, Integer>>> list = new ArrayList<>();
            a = 0;
            for (Output output : outputs) {
                List<Pair<Integer, Integer>> pairs = new ArrayList<>();
                List<Integer> integerList = flowInputs.get(output.getName());
                if (formal_outputs.containsKey(output.getName())) {
                    formal_outputs.put(output.getName(), i);
                }
                if (integerList != null) {
                    for (Integer stepIndex : integerList) {
                        Step step1 = steps.get(stepIndex);
                        if (stepIndex > i) {
                            Integer inputIndex = step1.getNameToInputIndex().get(output.getName());
                            Input input = step1.getInput(inputIndex);
                            if (input.getType().equals(output.getType())
                                    && !input.isConnected()) {
                                input.setConnected(true);
                                pairs.add(new Pair<>(stepIndex, inputIndex));
                            }
                        }
                    }
                }
                connections.get(i).get(a).addAll(pairs);
                a++;
            }
        }
    }


    public void CalculateFreeInputs() {
        flowFreeInputs = new HashMap<>();
        freeInputsIsReq = new HashMap<>();
        freeMandatoryInputs = new HashSet<>();
        for (String inputName : flowInputs.keySet()) {
            List<Integer> integerList = flowInputs.get(inputName);
            for (Integer stepIndex : integerList) {
                Step step = steps.get(stepIndex);
                Integer inputIndex = step.getNameToInputIndex().get(inputName);
                Input input = step.getInput(inputIndex);
                if (!input.isConnected()) {
                    if (!freeInputsIsReq.containsKey(inputName) && input.isMandatory()) {
                        freeInputsIsReq.put(inputName, true);
                        freeMandatoryInputs.add(inputName);
                    }

                    if (flowFreeInputs.containsKey(inputName))
                        flowFreeInputs.get(inputName).add(stepIndex);

                    else {
                        List<Integer> indexList = new ArrayList<>();
                        indexList.add(stepIndex);
                        flowFreeInputs.put(inputName, indexList);
                    }
                }
            }
            if (flowFreeInputs.containsKey(inputName) && !freeInputsIsReq.containsKey(inputName)) {
                freeInputsIsReq.put(inputName, false);
            }

        }
    }


    public Integer getStepIndexByName(String name)
    {
        return nameToIndex.get(name);
    }

    public Long getRunTime() {
        return runTime;
    }

    public String getActivationTime() {
        return activationTime;
    }

    public InputsDTO getInputList() {
        int i = 1;
        List<InputData> inputMenu = new ArrayList<>();
        for (String inputName : flowFreeInputs.keySet()) {
            Step step = steps.get(flowInputs.get(inputName).get(0));
            Integer inputIndex = step.getNameToInputIndex().get(inputName);
            String user_string = step.getInput(inputIndex).getUserString();
            Boolean necessity = freeInputsIsReq.get(inputName);

            inputMenu.add(new InputData(inputName, user_string, necessity));
        }
        return new InputsDTO(inputMenu, getName());
    }

    public boolean isFlowReady() {
        return (freeMandatoryInputs.isEmpty());
    }

    public ResultDTO processInput(String inputName, String rawData) {
        List<Integer> indexList = flowFreeInputs.get(inputName);
        String message;

        for (Integer stepIndex : indexList) {
            Step step = steps.get(stepIndex);
            Integer inputIndex = step.getNameToInputIndex().get(inputName);
            Input input = step.getInput(inputIndex);

            switch (input.getType()) {
                case "DataNumber":
                    try {
                        Integer number = Integer.parseInt(rawData);
                        input.setData(number);
                    } catch (NumberFormatException e) {
                        message = "the input was not processed successfully: "
                                + "expects to receive an integer only";
                        return new ResultDTO(false, message);
                    }
                    break;
                case "DataDouble":
                    try {
                        input.setData(Double.parseDouble(rawData));
                    } catch (NumberFormatException e) {
                        message = "the input was not processed successfully :"
                                + " expects to receive a real number only with a dot"
                                + " [for example: 2.0]";
                        return new ResultDTO(false, message);
                    }
                    break;
                case "DataString":
                    input.setData(rawData);
                    break;
            }
        }

        if (freeMandatoryInputs.contains(inputName))
            freeMandatoryInputs.remove(inputName);

        return new ResultDTO(true, "the input was processed successfully");
    }


    public Step getStep(int index) {
        return steps.get(index);
    }


    public void resetFlow() {
        state_after_run = null;
        runTime = null;
        flowId = null;
        activationTime = null;

        resetSteps();
        resetFreeMandatoryInputs();
    }


    private void resetSteps() {
        for (Step step : steps)
            step.resetStep();
    }

    private void resetFreeMandatoryInputs() {
        for (String inputName : freeInputsIsReq.keySet()) {
            if (freeInputsIsReq.get(inputName))
                freeMandatoryInputs.add(inputName);
        }
    }

    private void initConnections() {
        connections = new ArrayList<>();
        for (Step step : steps) {
            List<Output> outputs = step.getOutputs();
            List<List<Pair<Integer, Integer>>> list = new ArrayList<>();
            for (Output output : outputs) {
                List<Pair<Integer, Integer>> pairs = new ArrayList<>();
                list.add(pairs);
            }
            connections.add(list);
        }
    }


    private void initFlowInputs() {
        flowInputs = new HashMap<>();
        for (int i = 0; i < steps.size(); i++) {
            Step step = steps.get(i);
            List<Input> inputsList = step.getInputs();
            for (Input input : inputsList) {
                if (flowInputs.containsKey(input.getName())) {
                    flowInputs.get(input.getName()).add(i);
                } else {
                    List<Integer> indexList = new ArrayList<>();
                    indexList.add(i);
                    flowInputs.put(input.getName(), indexList);
                }
            }
        }
    }


    public String flowPrintData() //command 2
    {
        String data;
        data = "Flow name: " + name + "\n\n";
        data += "Flow description: " + description + "\n\n";
        data += getStrFormalOutputs() + "\n";
        data += getStrReadOnlyStatus() + "\n";
        data += getStrStepsData() + "\n";
        data += getStrFreeInputs() + "\n";
        data += getStrOutPuts() + "\n";
        return data;
    }


    public String getStrFormalOutputs() {
        String res;
        if (formal_outputs.size() > 0) {
            res = "The formal outputs of the flow are:\n";
            for (String currOutput : formal_outputs.keySet()) {
                res = res + currOutput + "\n";
            }
        } else
            res = "The flow doesn't have formal outputs\n";

        return res;
    }

    public String getStrReadOnlyStatus() {
        if (read_only)
            return "The flow is Read-Only: YES\n";
        else
            return "The flow is Read-Only: NO\n";
    }

    public String getStrStepsData() {
        String res = "THE FLOW'S STEPS:\n";
        String currStep;
        for (Step step : steps) {
            if (step.getName().equals(step.getDefaultName()))
                currStep = "Step name: " + step.getName() + "\n";
            else {
                currStep = "Step name: " + step.getDefaultName() + "\n";
                currStep += "Step alias: " + step.getName() + "\n";
            }
            if (step.isRead_only())
                currStep = currStep + "The step is Read-Only: YES\n";
            else
                currStep = currStep + "This step is Read-Only: No\n";
            currStep = currStep + "\n";
            res += currStep;
        }

        return res;
    }


    public String getStrFreeInputs() {
        String res;
        if (flowFreeInputs.isEmpty())
            res = "The flow have no free inputs\n";
        else {
            res = "Flow's free input's are:\n\n";
            String currInput;
            for (String key : flowFreeInputs.keySet()) {
                List<Integer> inputs = flowFreeInputs.get(key);
                int i = inputs.get(0);
                int inputIndex = steps.get(i).getNameToInputIndex().get(key);
                Input input = steps.get(i).getInput(inputIndex);
                currInput = "Name: " + input.getName() + "\n";
                currInput += "Type: " + input.getType() + "\n";
                currInput += "Steps that are related to that input: ";
                for (Integer j : inputs) {
                    currInput += steps.get(j).getName() + ", ";
                }
                currInput = currInput.substring(0, currInput.length() - 1);
                currInput += "\n";
                if (freeInputsIsReq.get(input.getName()))
                    currInput += "This input is mandatory: Yes\n\n";
                else
                    currInput += "This input is mandatory: No\n\n";
                res += currInput;
            }
        }

        return res;
    }


    public String getStrOutPuts() {
        String res = "THE FLOW'S OUTPUTS:\n";
        boolean isFound = false;
        List<Output> list;
        for (Step step : steps) {
            list = step.getOutputs();
            if (list.size() > 0)
                isFound = true;
            for (Output output : list) {
                res += "Output name: " + output.getName() + "\n";
                res += "Type: " + output.getType() + "\n";
                res += "Belong to step: " + step.getName() + "\n\n";
            }

        }

        if (isFound)
            return res;
        else
            return "THIS FLOW HAVE NO OUTPUTS";
    }


    public String executeFlow() {
        Long startTime = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.format(new Date());
        activationTime = formatter.format(new Date());
        boolean continueExecution = true;
        int outPutIndex;
        state_after_run = State.SUCCESS;
        for (int i = 0; i < steps.size() && continueExecution; i++) {
            Step currStep = steps.get(i);
            currStep.Run();
            if (currStep.getState_after_run() == State.FAILURE) {
                if (!currStep.isContinue_if_failing()) {
                    state_after_run = State.FAILURE;
                    continueExecution = false;
                }
            }
            if (continueExecution) {
                if (currStep.getState_after_run() == State.WARNING)
                    state_after_run = State.WARNING;
                List<List<Pair<Integer, Integer>>> stepConnections = connections.get(i);
                for (List<Pair<Integer, Integer>> currOutput : stepConnections) {
                    outPutIndex = 0;
                    for (Pair<Integer, Integer> currConnection : currOutput) {
                        int targetStepIndex = currConnection.getKey();
                        int targetStepInputIndex = currConnection.getValue();
                        steps.get(targetStepIndex).getInput(targetStepInputIndex).setData(currStep.getOutput(outPutIndex).getData());

                    }
                    outPutIndex++;
                }
            }
        }
        flowId = generateFlowId();
        runTime = System.currentTimeMillis() - startTime;
        return getFlowExecutionStrData();
    }


    public String generateFlowId() {
        UUID uuid = UUID.randomUUID();
        String uuidAsString = uuid.toString();
        return uuidAsString;
    }


    public String getFlowExecutionStrData() {
        String res = getFlowNameIDAndState();

        if (formal_outputs.size() > 0) {
            res += "FLOW'S FORMAL OUTPUTS:\n";
            for (String currOutput : formal_outputs.keySet()) {
                Step step = steps.get(formal_outputs.get(currOutput));
                int outPutIndex = step.getNameToOutputIndex().get(currOutput);
                res += step.getOutput(outPutIndex).getUserString() + "\n";
                if (step.getOutput(outPutIndex).getData() != null)
                    res += step.getOutput(outPutIndex).getData().toString() + "\n";
                else
                    res += "The output wasn't produced\n";

            }
        } else {
            res += "THE FLOW HAVE NO OUTPUTS\n";
        }

        return res;


    }

    public String getFlowNameIDAndState() {
        String res = "Flows unique ID: " + flowId + "\n";
        res += "Flow name: " + name + "\n";
        res += "Flow's final state : " + state_after_run + "\n";
        return res;
    }


    public String getFlowHistoryData() {
        String res = getFlowNameIDAndState();
        String temp;
        res += "Flow total run time: " + runTime + "\n\n";
        res += "FREE INPUTS THAT RECEIVED DATA:\n\n";
        temp = getFreeInputsHistoryData(true);
        temp += getFreeInputsHistoryData(false);
        if (temp.length() == 0)
            res += "NO FREE INPUTS HAVE RECEIVED DATA\n\n";
        else
            res += temp;
        res += "DATA PRODUCED (OUTPUTS):\n\n";
        temp = getOutputsHistoryData();
        if (temp.length() == 0)
            res += "NO DATA WAS PRODUCED\n\n";
        else
            res += temp;
        res += "FLOW STEPS DATA:\n\n";
        res += getStepsHistoryData();

        return res;
    }


    public String getFreeInputsHistoryData(boolean mandatoryOrNot) {
        String res = "";
        String currInput;
        for (String key : flowFreeInputs.keySet()) {
            List<Integer> inputs = flowFreeInputs.get(key);
            int i = inputs.get(0);
            int inputIndex = steps.get(i).getNameToInputIndex().get(key);
            Input input = steps.get(i).getInput(inputIndex);
            if (input.getData() != null) {
                currInput = "Name: " + input.getName() + "\n";
                currInput += "Type: " + input.getType() + "\n";
                currInput += "Input data:\n" + input.getData().toString() + "\n";
                if (freeInputsIsReq.get(input.getName()))
                    currInput += "This input is mandatory: Yes\n\n";
                else
                    currInput += "This input is mandatory: No\n\n";

                if (mandatoryOrNot && freeInputsIsReq.get(input.getName()))
                    res += currInput;
                else if (!mandatoryOrNot && !freeInputsIsReq.get(input.getName()))
                    res += currInput;
            }
        }

        return res;
    }

    public String getOutputsHistoryData() {
        String res = "";
        boolean flowStopped = false;
        for (int i = 0; i < steps.size() && !flowStopped; i++) {
            Step step = steps.get(i);
            List<Output> outputs = step.getOutputs();
            for (Output output : outputs) {
                if (output.getData() != null) {
                    res += "Name: " + output.getName() + "\n";
                    res += "Type: " + output.getType() + "\n";
                    res += "Data:\n" + output.getData().toString() + "\n\n";
                }
            }
            if (step.getState_after_run() == State.FAILURE && !step.isContinue_if_failing())
                flowStopped = true;
        }
        return res;
    }

    public String getStepsHistoryData() {
        String res = "";
        boolean flowStopped = false;
        for (int i = 0; i < steps.size() && !flowStopped; i++) {
            Step step = steps.get(i);
            res += step.getStepHistoryData();
            if (step.getState_after_run() == State.FAILURE && !step.isContinue_if_failing())
                flowStopped = true;
        }
        return res;
    }


    private void checkNoOutputWithSameNameAndFormalExists() {
        Set<String> outputs = new HashSet<>();
        boolean foundDuplicate = false;
        int formalOutputsCount = 0;

        for (int i = 0; i < steps.size() && !foundDuplicate; i++) {
            Step currStep = steps.get(i);
            for (Output output : currStep.getOutputs()) {
                if (!outputs.add(output.getName()))
                    foundDuplicate = true;
                if (formal_outputs.get(output.getName()) != null)
                    formalOutputsCount++;

            }
        }

        if (foundDuplicate) {
           throw new SameOutputNameException("The flow \"" + name +"\" contains the same output name for two different outputs");
        }
        if (formalOutputsCount != formal_outputs.size()) {
            throw new InputOutputNotExistException("The flow \"" + name +"\" contains a formal output that doesn't exists");
        }
    }


    private void checkMandatoryInputsAreFriendlyAndSameType() {
        for (String input : flowFreeInputs.keySet()) {
            String type = null;
            for (Integer i : flowFreeInputs.get(input)) {
                Input currInput = steps.get(i).getInputByName(input);
                if (currInput.isMandatory() && !currInput.isUser_friendly()) {
                    throw new MandatoryInputException("The free input:" + currInput.getName() + " in the flow:" + name + " is mandatory but isn't user-friendly");
                }
                if (type != null) {
                    if (!currInput.getType().equals(type)) {
                        throw new MappingDifferentTypesException("The free input:" + currInput.getName() + " in the flow:" + name + " have ambiguity in the input's type (required in different steps)");
                    }
                } else
                    type = currInput.getType();
            }
        }

        //or throw exception here
    }

    public void checkFlowIsValid()
    {
        checkMandatoryInputsAreFriendlyAndSameType();
        checkNoOutputWithSameNameAndFormalExists();
    }





    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRead_only() {
        return read_only;
    }

    public String getFlowId() {
        return flowId;
    }

    public State getState_after_run() {
        return state_after_run;
    }
}
