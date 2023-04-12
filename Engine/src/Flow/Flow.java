package Flow;

import DTO.InputData;
import DTO.InputsDTO;
import DataDefinitions.Input;
import DataDefinitions.Output;
import Steps.Step;
import javafx.util.Pair;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;


public class Flow implements Serializable
{
    public enum State
    {
        SUCCESS,
        WARNING,
        FAILURE
    }

    private String name;
    private String description;
    private boolean read_only;
    private String flowId;
    private Step.State state_after_run;
    private Long runTime;
    private String activationTime;
    private Map<String,Integer> formal_outputs;
    private List<Step> steps;
    private int numberOfSteps;
    private Map<String,Integer> nameToIndex;
    private List<List<List<Pair<Integer,Integer>>>> connections;
    private Map<String,List<Integer>> flowInputs;
    private Map<String,List<Integer>> flowFreeInputs;
    private Map<String,Boolean> freeInputsIsReq;
    private Set<String> freeMandatoryInputs;

    public Flow(String name, String description)
    {
        this.name = name;
        this.description = description;
        steps = new ArrayList<>();
        nameToIndex = new HashMap<>();
        formal_outputs = new HashMap<>();
        numberOfSteps=0;

    }

    public void AddStep(Step step)
    {
        steps.add(step);
        numberOfSteps++;
        nameToIndex.put(step.getName(),steps.size()-1);
    }

    public Integer getNumberOfSteps()
    {
        return numberOfSteps;
    }

    public void AddFormalOutput(String outputName)
    {
        formal_outputs.put(outputName,-1);
    }

    public void CustomMapping(Map<Pair<String,String>,Pair<String,String>> customMapping)
    {
        initConnections();
        for(Pair<String,String> key: customMapping.keySet())
        {
            Pair<String,String> currValue = customMapping.get(key);
            Integer outPutStepIndex = nameToIndex.get(key.getKey());
            Integer outPutIndex = steps.get(outPutStepIndex).getNameToOutputIndex().get(key.getValue());
            Integer inputStepIndex = nameToIndex.get(currValue.getKey());
            Integer inputIndex = steps.get(inputStepIndex).getNameToInputIndex().get(currValue.getValue());
            connections.get(outPutStepIndex).get(outPutIndex).add(new Pair<>(inputStepIndex,inputIndex));
            steps.get(inputStepIndex).getInput(inputIndex).setConnected(true);
        }

    }

    public void AutomaticMapping()
    {
        int a;
        read_only = true;

        initFlowInputs();
        if(connections == null)
            initConnections();

        for(int i=0;i<steps.size();i++)
        {
            Step step = steps.get(i);
            if(!step.isRead_only())
                read_only = false;
            List<Output> outputs =step.getOutputs();
            List<List<Pair<Integer,Integer>> > list=new ArrayList<>();
            a=0;
            for(Output output:outputs)
            {
                List<Pair<Integer,Integer>> pairs=new ArrayList<>();
                List<Integer> integerList = flowInputs.get(output.getName());
                if(formal_outputs.containsKey(output.getName()))
                {
                    formal_outputs.put(output.getName(),i);
                }
                if(integerList!=null)
                {
                    for (Integer stepIndex : integerList)
                    {
                        Step step1 = steps.get(stepIndex);
                        if (stepIndex > i)
                        {
                            Integer inputIndex = step1.getNameToInputIndex().get(output.getName());
                            Input input = step1.getInput(inputIndex);
                            if (input.getType().equals(output.getType())
                                    && !input.isConnected())
                            {
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


    public void CalculateFreeInputs()
    {
        flowFreeInputs = new HashMap<>();
        freeInputsIsReq =new HashMap<>();
        freeMandatoryInputs=new HashSet<>();
        for (String inputName:flowInputs.keySet())
        {
            List<Integer> integerList =flowInputs.get(inputName);
            for(Integer stepIndex:integerList)
            {
                Step step = steps.get(stepIndex);
                Integer inputIndex = step.getNameToInputIndex().get(inputName);
                Input input = step.getInput(inputIndex);
                if(!input.isConnected())
                {
                    if( !freeInputsIsReq.containsKey(inputName) && input.isMandatory() )
                    {
                            freeInputsIsReq.put(inputName,true);
                            freeMandatoryInputs.add(inputName);
                    }

                    if(flowFreeInputs.containsKey(inputName))
                        flowFreeInputs.get(inputName).add(stepIndex);

                    else
                    {
                        List<Integer> indexList = new ArrayList<>();
                        indexList.add(stepIndex);
                        flowFreeInputs.put(inputName,indexList);
                    }
                }
            }
            if(flowFreeInputs.containsKey(inputName)&&!freeInputsIsReq.containsKey(inputName))
            {
                freeInputsIsReq.put(inputName,false);
            }

        }
    }


    public Long getRunTime() {
        return runTime;
    }

    public String getActivationTime()
    {
        return activationTime;
    }

    public InputsDTO getInputList()
    {
        int i=1;
        List<InputData> inputMenu=new ArrayList<>();
        for (String inputName:flowFreeInputs.keySet())
        {
            Step step=steps.get(flowInputs.get(inputName).get(0));
            Integer inputIndex=step.getNameToInputIndex().get(inputName);
            String user_string=step.getInput(inputIndex).getUserString();
            Boolean necessity=freeInputsIsReq.get(inputName);

            inputMenu.add(new InputData(inputName,user_string,necessity));
        }
        return new InputsDTO(inputMenu,getName());
    }

    public boolean isFlowReady()
    {
        return(freeMandatoryInputs.isEmpty());
    }

    public void processInput(String inputName,String rawData)
    {
        if(freeMandatoryInputs.contains(inputName))
            freeMandatoryInputs.remove(inputName);


        List<Integer> indexList =flowFreeInputs.get(inputName);

        for(Integer stepIndex:indexList)
        {
            Step step =steps.get(stepIndex);
            Integer inputIndex =step.getNameToInputIndex().get(inputName);
            Input input=step.getInput(inputIndex);

            switch (input.getType())
            {
                case "DataNumber":
                    input.setData(Integer.parseInt(rawData));
                    break;
                case "DataDouble":
                    input.setData(Double.parseDouble(rawData));
                    break;
                case "DataString":
                    input.setData(rawData);
                    break;
            }

        }
    }


    public Step getStep(int index)
    {
        return steps.get(index);
    }


    public void resetFlow()
    {
        state_after_run=null;
        runTime=null;
        flowId=null;
        activationTime = null;

        for(Step step:steps)
            step.resetStep();

        for(String inputName:freeInputsIsReq.keySet())
        {
            if(freeInputsIsReq.get(inputName))
                freeMandatoryInputs.add(inputName);
        }
    }

    public void initConnections()
    {
        connections = new ArrayList<>();
        for (Step step : steps)
        {
            List<Output> outputs = step.getOutputs();
            List<List<Pair<Integer, Integer>>> list = new ArrayList<>();
            for (Output output : outputs) {
                List<Pair<Integer, Integer>> pairs = new ArrayList<>();
                list.add(pairs);
            }
            connections.add(list);
        }
    }


    public void initFlowInputs()
    {
        flowInputs = new HashMap<>();
        for(int i = 0; i< steps.size(); i++)
        {
            Step step = steps.get(i);
            List<Input> inputsList = step.getInputs();
            for(Input input : inputsList)
            {
                if(flowInputs.containsKey(input.getName()))
                {
                    flowInputs.get(input.getName()).add(i);
                }
                else
                {
                    List<Integer> indexList = new ArrayList<>();
                    indexList.add(i);
                    flowInputs.put(input.getName(),indexList);
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


    public String getStrFormalOutputs()
    {
        String res;
        if(formal_outputs.size() > 0) {
            res = "The formal outputs of the flow are:\n";
            for (String currOutput: formal_outputs.keySet()) {
                res = res + currOutput + "\n";
            }
        }
        else
            res = "The flow doesn't have formal outputs\n";

        return res;
    }

    public String getStrReadOnlyStatus()
    {
        if(read_only)
            return "The flow is Read-Only: YES\n";
        else
            return "The flow is Read-Only: NO\n";
    }

    public String getStrStepsData()
    {
        String res = "THE FLOW'S STEPS:\n";
        String currStep;
        for(Step step: steps)
        {
            if(step.getName().equals(step.getDefaultName()))
                currStep = "Step name: " + step.getName() + "\n";
            else
            {
                currStep = "Step name: " + step.getDefaultName() + "\n";
                currStep += "Step alias: " + step.getName() + "\n";
            }
            if(step.isRead_only())
                currStep = currStep +  "The step is Read-Only: YES\n";
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
        else
        {
            res = "Flow's free input's are:\n\n";
            String currInput;
            for (String key : flowFreeInputs.keySet())
            {
                List<Integer> inputs = flowFreeInputs.get(key);
                int i = inputs.get(0);
                int inputIndex = steps.get(i).getNameToInputIndex().get(key);
                Input input = steps.get(i).getInput(inputIndex);
                currInput = "Name: " + input.getName() + "\n";
                currInput += "Type: " + input.getType() + "\n";
                currInput += "Steps that are related to that input: ";
                for (Integer j : inputs)
                {
                    currInput +=  steps.get(j).getName() + ", ";
                }
                currInput = currInput.substring(0,currInput.length() - 1);
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


    public String getStrOutPuts()
    {
        String res = "THE FLOW'S OUTPUTS:\n";
        boolean isFound = false;
        List<Output> list;
        for(Step step:steps)
        {
            list = step.getOutputs();
            if(list.size() > 0)
                isFound = true;
            for(Output output: list)
            {
                res += "Output name: " + output.getName() + "\n";
                res += "Type: " + output.getType() + "\n";
                res += "Belong to step: " + step.getName() + "\n\n";
            }

        }

        if(isFound)
            return res;
        else
            return "THIS FLOW HAVE NO OUTPUTS";
    }


    public String executeFlow()
    {
        Long startTime =  System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.format(new Date());
        activationTime = formatter.format(new Date());
        boolean continueExecution = true;
        int outPutIndex;
        state_after_run = Step.State.SUCCESS;
        for(int i=0;i<steps.size() && continueExecution;i++)
        {
            Step currStep = steps.get(i);
            currStep.Run();
            if(currStep.getState_after_run() == Step.State.FAILURE) {
                if (!currStep.isContinue_if_failing()) {
                    state_after_run = Step.State.FAILURE;
                    continueExecution = false;
                }
            }
            if(continueExecution)
            {
                    if(currStep.getState_after_run() == Step.State.WARNING)
                        state_after_run = Step.State.WARNING;
                    List<List<Pair<Integer,Integer>>> stepConnections =  connections.get(i);
                    for(List<Pair<Integer,Integer>> currOutput : stepConnections)
                    {
                        outPutIndex = 0;
                        for(Pair<Integer,Integer> currConnection: currOutput)
                        {
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


    public String generateFlowId()
    {
        UUID uuid = UUID.randomUUID();
        String uuidAsString = uuid.toString();
        return uuidAsString;
    }


    public String getFlowExecutionStrData()
    {
        String res = getFlowNameIDAndState();

        if(formal_outputs.size() > 0) {
            res += "FLOW'S FORMAL OUTPUTS:\n";
            for (String currOutput : formal_outputs.keySet()) {
                int outPutIndex = steps.get(formal_outputs.get(currOutput)).getNameToOutputIndex().get(currOutput);
                res += steps.get(formal_outputs.get(currOutput)).getOutput(outPutIndex).getUserString() + "\n";
                res += steps.get(formal_outputs.get(currOutput)).getOutput(outPutIndex).getData().toString() + "\n";

            }
        }
        else {
            res += "THE FLOW HAVE NO OUTPUTS\n";
        }

        return res;


    }

    public String getFlowNameIDAndState()
    {
        String res = "Flows unique ID: " + flowId + "\n";
        res += "Flow name: " + name + "\n";
        res += "Flow's final state : " + state_after_run + "\n";
        return res;
    }


    public String getFlowHistoryData()
    {
        String res = getFlowNameIDAndState();
        String temp;
        res += "Flow total run time: " + runTime + "\n\n";
        res += "FREE INPUTS THAT RECEIVED DATA:\n\n";
        temp = getFreeInputsHistoryData(true);
        temp += getFreeInputsHistoryData(false);
        if(temp.length() == 0)
            res += "NO FREE INPUTS HAVE RECEIVED DATA\n\n";
        else
            res += temp;
        res += "DATA PRODUCED (OUTPUTS):\n\n";
        temp = getOutputsHistoryData();
        if(temp.length() == 0)
            res += "NO DATA WAS PRODUCED\n\n";
        else
            res += temp;
        res += "FLOW STEPS DATA:\n\n";
        res += getStepsHistoryData();

        return res;
    }


    public String getFreeInputsHistoryData(boolean mandatoryOrNot)
    {
        String res = "";
        String currInput;
        for (String key : flowFreeInputs.keySet())
        {
            List<Integer> inputs = flowFreeInputs.get(key);
            int i = inputs.get(0);
            int inputIndex = steps.get(i).getNameToInputIndex().get(key);
            Input input = steps.get(i).getInput(inputIndex);
            if(input.getData() != null) {
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

    public String getOutputsHistoryData()
    {
        String res = "";
        for(Step step: steps)
        {
            List<Output> outputs = step.getOutputs();
            for(Output output : outputs)
            {
                if(output.getData() != null)
                {
                    res += "Name: " + output.getName() + "\n";
                    res+= "Type: " + output.getType() + "\n";
                    res += "Data:\n" + output.getData().toString() + "\n\n";
                }
            }
        }
        return res;
    }

    public String getStepsHistoryData()
    {
        String res = "";
        for(Step step: steps)
        {
            res += step.getStepHistoryData();
        }
        return res;
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

    public Step.State getState_after_run() {
        return state_after_run;
    }
}
