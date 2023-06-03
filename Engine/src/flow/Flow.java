package flow;

import datadefinition.DataDefinition;
import datadefinition.DataEnumerator;
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
    private Map<String,String> initialValues;

    private Map<String,Integer> flowOutputs;

    private Map<String, Continuation> continuations;

    public Flow(String name, String description) {
        this.name = name;
        this.description = description;
        steps = new ArrayList<>();
        nameToIndex = new HashMap<>();
        formalOutputs = new HashMap<>();
        numberOfSteps = 0;
        continuations = null;
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
                        + name + "\" contains mapping from the step: " + currValue.getKey() + " to the step: " + key.getKey() +
                        " while the step: " + key.getKey() + " is executed in the flow before the step: " + currValue.getKey());
            }

            if (!(steps.get(outPutStepIndex).getOutput(outPutIndex).getType().equals(steps.get(inputStepIndex).getInput(inputIndex).getType()))) {
                throw new StepsMappingOrderException("The Custom mapping in the flow \"" + name
                        + "\" contains mapping for the input: " + key.getValue() + "\nfrom the output: "
                        + currValue.getValue() + " while the input and output have data of different types");
            }

            if(steps.get(inputStepIndex).getInput(inputIndex).haveInitialValue()) {
                throw new InitialValueException("The Custom mapping in the flow \"" + name
                        + "\" contains mapping for the input: " + key.getValue() + "\nfrom the output: "
                        + currValue.getValue() + " while the input already have an initial value");
            }


            connections.get(outPutStepIndex).get(outPutIndex).add(new Pair<>(inputStepIndex, inputIndex));
            steps.get(inputStepIndex).getInput(inputIndex).setConnected(true);
        }
    }


    private void checkInputDataValid(Pair<String, String> key, Pair<String, String> currValue, Integer inputIndex) {
        if (inputIndex == null) {
            throw new InputOutputNotExistException("The Custom mapping in the flow \"" + name
                    + "\" contains mapping for a step's input that doesn't exist\nstep name:"
                    + currValue.getKey() + ", input name: " + key.getValue());
        }
    }

    private void checkIfOutputDataValid(Pair<String, String> currValue, Integer outPutIndex) {
        if (outPutIndex == null) {
            throw new InputOutputNotExistException("The Custom mapping in the flow \"" + name
                    + "\" contains mapping for a step's output that doesn't exist, step name: "
                    + currValue.getKey() + ", output name: " + currValue.getValue());
        }
    }

    private void checkIfStepValid(Pair<String, String> pair, Integer StepIndex) {
        if (StepIndex == null) {
            throw new StepNameNotExistException("The Custom mapping in the flow \"" + name
                    + "\" contains mapping for a step that doesn't exist, step name: " + pair.getKey());
        }
    }


    public void automaticMapping() {
        int a;
        readOnly = true;

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
                    if (input.getType().equals(output.getType()) && !input.isConnected() && !input.haveInitialValue()) {
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
                if (!input.isConnected() && !input.haveInitialValue()) {
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
            Input input=step.getInput(inputIndex);
            String user_string = input.getUserString();
            boolean inserted= input.getData() != null;
            Boolean necessity = freeInputsIsReq.get(inputName);
            inputMenu.add(new InputData(inputName, user_string, necessity,inserted));
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

    public InputData clearInputData(String inputName)
    {
        List<Integer> indexList = flowFreeInputs.get(inputName);
        if(indexList!=null) {
            String userString=null;
            for(int stepIndex:indexList){
                Step step = steps.get(stepIndex);
                Integer inputIndex = step.getNameToInputIndex().get(inputName);
                Input input = step.getInput(inputIndex);
                input.setData(null);
                userString=input.getUserString();
            }
            if(freeInputsIsReq.get(inputName))
                freeMandatoryInputs.add(inputName);
            return new InputData(inputName,userString,freeInputsIsReq.get(inputName),false);
        }
        return null;
    }

    public FreeInputExecutionDTO getInputData(String inputName) {

        List<Integer> inputs = flowFreeInputs.get(inputName);
        int resIndex = -1;
        for(int stepIndex : inputs) {
            Step step = steps.get(stepIndex);
            int inputIndex = step.getNameToInputIndex().get(inputName);
            Input input = step.getInput(inputIndex);
            if(input.getDefaultName().equals("FILE_NAME") || input.getDefaultName().equals("FOLDER_NAME"))
                resIndex = stepIndex;
        }


        Step step;
        if(resIndex == -1)
            step = steps.get(inputs.get(0));
        else
            step = steps.get(resIndex);

        int inputIndex = step.getNameToInputIndex().get(inputName);
        Input input = step.getInput(inputIndex);


//        int stepIndex = flowFreeInputs.get(inputName).get(0);
//        Step step = steps.get(stepIndex);
//        int inputIndex = step.getNameToInputIndex().get(inputName);
//        Input input = step.getInput(inputIndex);

        String data=null;
        if(input.getData()!=null)
            data=input.getData().toString();

        DataDefintionDTO dataDefintionDTO = new DataDefintionDTO(input.getName(), input.getType());
        return new FreeInputExecutionDTO(dataDefintionDTO, data, freeInputsIsReq.get(inputName));
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
            case "DataEnumerator":
                try {
                    input.setData(rawData);
                }
                catch (EnumerationDataException e) {
                    message = "Input processing failed due to: "
                            + "this enumeration data expects to receive one of the following only: " + e.getMessage();
                    return new ResultDTO(false,message);
                }
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

    private Map<String,StepConnectionsDTO> getStepsConnectionsDTO() {
        Map<String, Map<String,Map<String,String>>> outputsConnections = new LinkedHashMap<>();
        Map<String, Map<String,Pair<String,String>>> inputsConnections = new LinkedHashMap<>();
        List<Output> list;
        for (int i = 0 ;i<steps.size();i++) {
            Map<String,Map<String,String>> outputs = new LinkedHashMap<>();
            Step step = steps.get(i);
            list = step.getOutputs();
            for (int j = 0; j<list.size();j++) {
                Output output = list.get(j);
                List<Pair<Integer,Integer>> currConnections = connections.get(i).get(j);
                Map<String,String> currOutputConnections = new LinkedHashMap<>();
                for(Pair<Integer,Integer> currPair : currConnections)
                {
                    String inputName = steps.get(currPair.getKey()).getInput(currPair.getValue()).getName();
                    String stepName = steps.get(currPair.getKey()).getName();
                    currOutputConnections.put(stepName,inputName);
                    Pair<String,String> currInput = new Pair<>(step.getName(),output.getName());
                    if(inputsConnections.containsKey(stepName))
                        inputsConnections.get(stepName).put(inputName, currInput);
                    else {
                        Map<String,Pair<String,String>> inputConnection = new LinkedHashMap<>();
                        inputConnection.put(inputName, currInput);
                        inputsConnections.put(stepName, inputConnection);
                    }
                }
                outputs.put(output.getName(),currOutputConnections);
            }
            outputsConnections.put(step.getName(),outputs);
        }
        return createConnectionsDTO(outputsConnections,inputsConnections);
    }

    private Map<String,StepConnectionsDTO> createConnectionsDTO(Map<String, Map<String, Map<String, String>>> outputsConnections, Map<String, Map<String, Pair<String, String>>> inputsConnections) {
        Map<String,StepConnectionsDTO> connections = new LinkedHashMap<>();
        for (Step step : steps) {
            Map<String, Map<String, String>> currOConnections = outputsConnections.get(step.getName());
            Map<String, Pair<String, String>> currIConnections = inputsConnections.get(step.getName());
            Map<String, Boolean> isMandatory = new LinkedHashMap<>();
            for (Input input :step.getInputs()) {
                isMandatory.put(input.getName(), input.isMandatory());
            }
            StepConnectionsDTO stepConnectionsDTO = new StepConnectionsDTO(currOConnections,currIConnections,isMandatory);
            connections.put(step.getName(), stepConnectionsDTO);
        }

        return connections;
    }




    private List<StepDefinitionDTO> getStepsDefinitionDTO()
    {
        Map<String,StepConnectionsDTO> connections= getStepsConnectionsDTO();
        List<StepDefinitionDTO> stepsList = new ArrayList<>();
        for(Step step: steps)
        {
            stepsList.add(new StepDefinitionDTO(step.getName(),step.getDefaultName(),step.isRead_only(),connections.get(step.getName())));
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


    public void setFlowOutputs() {
        Map<String,Integer> flowOutputs = new HashMap<>();
        for(int i = 0; i< steps.size();i++) {
            Step currStep = steps.get(i);
            List<Output> currOutputs = currStep.getOutputs();
            for(Output output: currOutputs) {
                flowOutputs.put(output.getName(),i);
            }

        }
        this.flowOutputs = flowOutputs;
    }


    public boolean checkIfFlowContainsInput(String inputName)
    {
        return flowInputs.containsKey(inputName);
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





    public int getNumberOfInputs() {
        int count = 0;
        if(flowFreeInputs == null)
            return count;
        for(String input: flowFreeInputs.keySet()) {
            count = count + flowFreeInputs.get(input).size();
        }

        return count;
    }

    public int getNumberOfContinuations() {
        if(continuations == null)
            return 0;
        else
            return continuations.size();
    }

    public void setInitialValues(Map<String, String> initialValues) {
        initFlowInputs();
        for (String name : initialValues.keySet()) {
            if (!flowInputs.containsKey(name))
                throw new InitialValueException("The flow \"" + this.name + "\" contains an initial value to an input that doesn't exists (input:" + name + ")");
        }
        this.initialValues = initialValues;
        applyInitialValues();
    }

    private void applyInitialValues() {
        for(String inputName : initialValues.keySet()) {
           List<Integer> targetInputs =  flowInputs.get(inputName);
           for(int stepIndex: targetInputs) {
               Step currStep = steps.get(stepIndex);
               Input currInput = currStep.getInputByName(inputName);
               if(!currInput.isUser_friendly())
                   throw new InitialValueDataTypeException("Initial values contains a value for a non user-friendly input, input name: " + inputName);
               if(!SetInputData(initialValues.get(inputName), currInput).getStatus()) {
                   if(!currInput.getType().equals("DataEnumerator"))
                         throw new InitialValueDataTypeException("The initial value for the input \"" + inputName + "\" have the wrong data type\n" +
                           "the correct data type is: " + currInput.getType().substring(4));
                   else
                       throw new InitialValueDataTypeException("The initial value for the input \"" + inputName + "\" have a wrong value\n" +
                           "this input is an enumerator data type and the entered values isn't allowed" );
               }
               currInput.setHaveInitialValue(true);
           }
        }
    }


    public Map<String, Integer> getFlowOutputs() {
        return flowOutputs;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public Map<String, List<Integer>> getFlowFreeInputs() {
        return flowFreeInputs;
    }

    public Map<String, Continuation> getContinuations() {
        return continuations;
    }

    public ContinutionMenuDTO getContinutionMenuDTO()
    {
        List<String> targetFlows=new ArrayList<>();

        if(continuations!=null) {
            for (String targetFlow : continuations.keySet()) {
                targetFlows.add(targetFlow);
            }
            return new ContinutionMenuDTO(targetFlows);
        }
        return  null;
    }

    public void setContinuations(List<Continuation> continuations) {
        this.continuations = new LinkedHashMap<>();
        for(Continuation continuation : continuations)
        {
            this.continuations.put(continuation.getTargetFlow(),continuation);
        }
    }

    public Continuation getContinuation(String targetName) {
        return continuations.get(targetName);
    }

    public void applyContinuation(FlowExecution flowExecution, Continuation continuation) {
        Map<Pair<Integer,String>,List<Pair<Integer,String>>> continuationMapping = continuation.getContinuationMapping();
        Map<Pair<Integer,String>,List<Pair<Integer,String>>> continuationForcedMapping = continuation.getContinuationForcedMapping();
        List<Step> sourceSteps = flowExecution.getSteps();

        setContinuationData(continuationMapping, sourceSteps);
        setContinuationData(continuationForcedMapping, sourceSteps);
    }

    private void setContinuationData(Map<Pair<Integer, String>, List<Pair<Integer, String>>> continuationMapping, List<Step> sourceSteps) {
        for(Pair<Integer,String> currData : continuationMapping.keySet()) {
            List<Pair<Integer,String>> currTargets = continuationMapping.get(currData);
            Object data;
            Input input;
            try {
                input = sourceSteps.get(currData.getKey()).getInputByName(currData.getValue());
                data = input.getData();
            }
            catch (Exception ignored) {
                data =  sourceSteps.get(currData.getKey()).getOutputByName(currData.getValue()).getData();
            }

            for(Pair<Integer,String> currTarget : currTargets)
            {
                if(!steps.get(currTarget.getKey()).getInputByName(currTarget.getValue()).haveInitialValue()) {
                    steps.get(currTarget.getKey()).getInputByName(currTarget.getValue()).setData(data);
                    freeMandatoryInputs.remove(currTarget.getValue());
                }
            }
        }
    }

    public List<String> getEnumerationAllowedValues(String inputName) {
        int stepIndex = flowFreeInputs.get(inputName).get(0);
        Step step = steps.get(stepIndex);
        int inputIndex = step.getNameToInputIndex().get(inputName);
        Input input = step.getInput(inputIndex);

        return  input.getDataDefinition().getSecondaryData();
    }

    public String getInputDefaultName(String inputName) {
        int stepIndex = flowFreeInputs.get(inputName).get(0);
        Step step = steps.get(stepIndex);
        int inputIndex = step.getNameToInputIndex().get(inputName);
        Input input = step.getInput(inputIndex);

        return input.getDefaultName();
    }
}
