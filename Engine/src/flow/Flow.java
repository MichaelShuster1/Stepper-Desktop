package flow;

import dto.*;
import datadefinition.Input;
import datadefinition.Output;
import step.State;
import step.Step;
import exception.*;
import javafx.util.Pair;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;


public class Flow implements Serializable {
    private final String name;
    private final String description;
    private boolean readOnly;
    private final Map<String, Integer> formalOutputs;
    private final List<Step> steps;
    private int numberOfSteps;
    private final Map<String, Integer> nameToIndex;
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
        formalOutputs = new HashMap<>();
        numberOfSteps = 0;

    }

    public void addStep(Step step) {
        steps.add(step);
        numberOfSteps++;
        nameToIndex.put(step.getName(), steps.size() - 1);
    }

    public Integer getNumberOfSteps() {
        return numberOfSteps;
    }

    public List<List<Pair<Integer, Integer>>> getStepConnections(int index)
    {
        return connections.get(index);
    }

    public Map<String, Integer> getFormalOutputs()
    {
        return formalOutputs;
    }

    public Map<String, List<Integer>> getFlowFreeInputs()
    {
        return  flowFreeInputs;
    }


    public void addFormalOutput(String outputName) {
        formalOutputs.put(outputName, -1);
    }

    public void customMapping(Map<Pair<String, String>, Pair<String, String>> customMapping) {
        initConnections();

        for (Pair<String, String> key : customMapping.keySet()) {
            Pair<String, String> currValue = customMapping.get(key);
            Integer outPutStepIndex = nameToIndex.get(currValue.getKey());

            checkIfStepValid(currValue, outPutStepIndex);

            Integer outPutIndex = steps.get(outPutStepIndex).getNameToOutputIndex().get(currValue.getValue());

            checkIfOutputDataValid(currValue, outPutIndex);

            Integer inputStepIndex = nameToIndex.get(key.getKey());

            checkIfStepValid(key,inputStepIndex);

            Integer inputIndex = steps.get(inputStepIndex).getNameToInputIndex().get(key.getValue());

            checkInputDataValid(key, currValue, inputIndex);

            if (outPutStepIndex >= inputStepIndex) {
                throw new StepsMappingOrderException("The Custom mapping in the flow \""
                        + name + "\" contains mapping from the step:" + currValue.getKey() + " to the step:" + key.getKey() +
                        " while the step:" + key.getKey() + " is executed in the flow before the step:" + currValue.getKey());
            }

            if (!(steps.get(outPutStepIndex).getOutput(outPutIndex).getType().equals(steps.get(inputStepIndex).getInput(inputIndex).getType()))) {
                throw new StepsMappingOrderException("The Custom mapping in the flow \"" + name
                        + "\" contains mapping for the input:" + key.getValue() + "\nfrom the output:"
                        + currValue.getValue() + " while the input and output have data of different types");
            }


            connections.get(outPutStepIndex).get(outPutIndex).add(new Pair<>(inputStepIndex, inputIndex));
            steps.get(inputStepIndex).getInput(inputIndex).setConnected(true);
        }
    }


    private void checkInputDataValid(Pair<String, String> key, Pair<String, String> currValue, Integer inputIndex) {
        if (inputIndex == null) {
            throw new InputOutputNotExistException("The Custom mapping in the flow \"" + name
                    + "\" contains mapping for a step's input that doesn't exist\nstep name:"
                    + currValue.getKey() + ", input name:" + key.getValue());
        }
    }

    private void checkIfOutputDataValid(Pair<String, String> currValue, Integer outPutIndex) {
        if (outPutIndex == null) {
            throw new InputOutputNotExistException("The Custom mapping in the flow \"" + name
                    + "\" contains mapping for a step's output that doesn't exist, step name:"
                    + currValue.getKey() + ", output name:" + currValue.getValue());
        }
    }

    private void checkIfStepValid(Pair<String, String> pair, Integer StepIndex) {
        if (StepIndex == null) {
            throw new StepNameNotExistException("The Custom mapping in the flow \"" + name
                    + "\" contains mapping for a step that doesn't exist, step name:" + pair.getKey());
        }
    }


    public void automaticMapping() {
        int a;
        readOnly = true;

        initFlowInputs();
        if (connections == null)
            initConnections();

        for (int i = 0; i < steps.size(); i++) {
            Step step = steps.get(i);
            if (!step.isRead_only())
                readOnly = false;
            mapStepOutputsToInputs(i, step.getOutputs());
        }
    }

    private void mapStepOutputsToInputs(int index, List<Output> outputs) {
        int a=0;
        for (Output output : outputs) {
            if (formalOutputs.containsKey(output.getName())) {
                formalOutputs.put(output.getName(), index);
            }
            List<Pair<Integer, Integer>> pairs = getListPairsOfTargetInputs(index, output);
            connections.get(index).get(a).addAll(pairs);
            a++;
        }
    }

    private List<Pair<Integer, Integer>> getListPairsOfTargetInputs(int index, Output output) {
        List<Pair<Integer, Integer>> pairs = new ArrayList<>();
        List<Integer> integerList = flowInputs.get(output.getName());
        if (integerList != null) {
            for (Integer stepIndex : integerList) {
                Step step = steps.get(stepIndex);
                if (stepIndex > index) {
                    Integer inputIndex = step.getNameToInputIndex().get(output.getName());
                    Input input = step.getInput(inputIndex);
                    if (input.getType().equals(output.getType()) && !input.isConnected()) {
                        input.setConnected(true);
                        pairs.add(new Pair<>(stepIndex, inputIndex));
                    }
                }
            }
        }
        return pairs;
    }

    public void calculateFreeInputs() {
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


    public Integer getStepIndexByName(String name) {
        return nameToIndex.get(name);
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
        for (Integer stepIndex : indexList) {
            Step step = steps.get(stepIndex);
            Integer inputIndex = step.getNameToInputIndex().get(inputName);
            Input input = step.getInput(inputIndex);

            ResultDTO resultDTO = SetInputData(rawData, input);
            if (!resultDTO.getStatus())
                return resultDTO;
        }
        freeMandatoryInputs.remove(inputName);
        return new ResultDTO(true, "The input was processed successfully");
    }


    private ResultDTO SetInputData(String rawData, Input input) {
        String message;
        switch (input.getType()) {
            case "DataNumber":
                try {
                    Integer number = Integer.parseInt(rawData);
                    input.setData(number);
                } catch (NumberFormatException e) {
                    message = "Input processing failed due to: "
                            + "expects to receive an integer only";
                    return new ResultDTO(false, message);
                }
                break;
            case "DataDouble":
                try {
                    Double realNumber= Double.parseDouble(rawData);
                    input.setData(realNumber);
                } catch (NumberFormatException e) {
                    message = "Input processing failed due to:"
                            + " expects to receive a real number only with a dot"
                            + " [for example: 2.0]";
                    return new ResultDTO(false, message);
                }
                break;
            case "DataString":
                input.setData(rawData);
                break;
        }

        return new ResultDTO(true, "The input was processed successfully");
    }


    public Step getStep(int index) {
        return steps.get(index);
    }


    public void resetFlow() {

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


    public FlowDefinitionDTO getFlowDefinition()
    {
        FlowDetailsDTO details = new FlowDetailsDTO(name,description, formalOutputs.keySet(), readOnly);
        List<StepDefinitionDTO> steps = getStepsDefinitionDTO();
        List<FreeInputDefinitionDTO> freeInputs = getFreeInputsDefinitionDTO();
        List<OutputDefintionDTO> outputs = getOutputsDefinitionDTO();
        return new FlowDefinitionDTO(details,steps,freeInputs,outputs);
    }

    private List<OutputDefintionDTO> getOutputsDefinitionDTO()
    {
        List<OutputDefintionDTO> outputsList=new ArrayList<>();
        List<Output> list;
        for (Step step : steps) {
            list = step.getOutputs();
            for (Output output : list) {
                DataDefintionDTO dataDefintionDTO= new DataDefintionDTO(output.getName(), output.getType());
                OutputDefintionDTO outputDefintionDTO=new OutputDefintionDTO(dataDefintionDTO,step.getName());
                outputsList.add(outputDefintionDTO);
            }
        }
        return  outputsList;
    }

    private List<StepDefinitionDTO> getStepsDefinitionDTO()
    {
        List<StepDefinitionDTO> stepsList = new ArrayList<>();
        for(Step step: steps)
        {
            stepsList.add(new StepDefinitionDTO(step.getName(),step.getDefaultName(),step.isRead_only()));
        }
        return stepsList;
    }

    private List<FreeInputDefinitionDTO> getFreeInputsDefinitionDTO()
    {
        List<FreeInputDefinitionDTO> freeInputsList = new ArrayList<>();
        for (String key : flowFreeInputs.keySet()) {
            List<Integer> inputs = flowFreeInputs.get(key);
            int i = inputs.get(0);
            int inputIndex = steps.get(i).getNameToInputIndex().get(key);
            Input input = steps.get(i).getInput(inputIndex);
            List<String> relatedSteps = new ArrayList<>();
            for (Integer j : inputs) {
                relatedSteps.add(steps.get(j).getName());
            }
            DataDefintionDTO inputData = new DataDefintionDTO(input.getName(),input.getType());
            freeInputsList.add(new FreeInputDefinitionDTO(inputData,relatedSteps,input.isMandatory()));
        }

        return freeInputsList;
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
                if (formalOutputs.get(output.getName()) != null)
                    formalOutputsCount++;

            }
        }

        if (foundDuplicate) {
            throw new SameOutputNameException("The flow \"" + name + "\" contains the same output name for two different outputs");
        }
        if (formalOutputsCount != formalOutputs.size()) {
            throw new InputOutputNotExistException("The flow \"" + name + "\" contains a formal output that doesn't exists");
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
    }

    public void checkFlowIsValid() {
        checkMandatoryInputsAreFriendlyAndSameType();
        checkNoOutputWithSameNameAndFormalExists();
    }


    public String getName() {
        return name;
    }


    public String getDescription() {
        return description;
    }


    public boolean isReadOnly() {
        return readOnly;
    }


    public List<Step> getSteps() {
        return steps;
    }
}
