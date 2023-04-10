package Flow;

import DataDefinitions.Input;
import DataDefinitions.Output;
import Steps.Step;
import javafx.util.Pair;

import java.util.*;

public class Flow
{
    public enum State
    {
        SUCCESS,
        WARNING,
        FAILURE
    }

    public class FlowStatistics
    {
        private Integer amount_times_activated;
        private Long sum_of_run_time;

        public FlowStatistics()
        {
            amount_times_activated=0;
            sum_of_run_time=0L;
        }

        public void addRunTime(Long runTIme)
        {
            sum_of_run_time+=runTIme;
            amount_times_activated++;
        }

        public Integer getAmount_times_activated()
        {
            return amount_times_activated;
        }

        public Double getAvgRunTime()
        {
            return (double) (sum_of_run_time/amount_times_activated);
        }
    }

    private FlowStatistics flowStatistics;
    private String name;
    private String description;
    private boolean read_only;
    private String flowId;
    private Step.State state_after_run;
    private Long runTime;
    private Map<String,Integer> formal_outputs;
    private List<Step> steps;
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
        flowStatistics=new FlowStatistics();
    }

    public void AddStep(Step step)
    {
        steps.add(step);
        nameToIndex.put(step.getName(),steps.size()-1);
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

    /*
    public void AutomaticMapping()
    {
        initFlowInputs();
        int b;
        int a;
        for(int i=0;i<steps.size();i++)
        {
            Step step = steps.get(i);
            System.out.println("in step: "+step.getName());
            List<Output> outputs =step.getOutputs();
            a = 0;
            for(Output output:outputs)
            {
                System.out.println("the output "+ output.getName()+ " connects to the following inputs");
                List<Pair<Integer,Integer>> pairs=new ArrayList<>();
                for(int j=i+1;j<steps.size();j++)
                {
                    step=steps.get(j);
                    List<Input> inputs =step.getInputs();
                    b=0;
                    for (Input input:inputs)
                    {
                        if(input.getName().equals(output.getName())
                                && input.getType().equals(output.getType())
                                && !input.isConnected())
                        {
                            pairs.add(new Pair<>(j,b));
                            System.out.println(step.getName()+": "+input.getName());
                        }
                        b++;
                    }

                }
                connections.get(i).get(a).addAll(pairs);
                a++;
            }
        }
    }
*/

    public void AutomaticMapping()
    {
        initFlowInputs();
        read_only = true;
        int a;
        for(int i=0;i<steps.size();i++)
        {
            Step step = steps.get(i);
            if(!step.isRead_only())
                read_only = false;
            System.out.println("in step: "+step.getName());
            List<Output> outputs =step.getOutputs();
            List<List<Pair<Integer,Integer>> > list=new ArrayList<>();
            a=0;
            for(Output output:outputs)
            {
                System.out.println("the output "+ output.getName()+ " connects to the following inputs");
                List<Pair<Integer,Integer>> pairs=new ArrayList<>();
                List<Integer> integerList =flowInputs.get(output.getName());
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
                                System.out.println(step1.getName() + ": " + input.getName());
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

    public List<String> getInputList()
    {
        int i=1;
        List<String> inputMenu=new ArrayList<>();
        for (String inputName:flowFreeInputs.keySet())
        {
            if(freeInputsIsReq.get(inputName))
                inputMenu.add(inputName +" mandatory");
            else
                inputMenu.add(inputName +" optional");
        }
        return inputMenu;
    }

    public boolean checkIfFlowReady()
    {
        return(freeMandatoryInputs.isEmpty());
    }


    public void processInput(String inputName,Object data)
    {
        if(freeMandatoryInputs.contains(inputName))
            freeMandatoryInputs.remove(inputName);
        List<Integer> indexList =flowFreeInputs.get(inputName);

        for(Integer stepIndex:indexList)
        {
            Step step =steps.get(stepIndex);
            Integer inputIndex =step.getNameToInputIndex().get(inputName);
            step.getInput(inputIndex).setData(data);
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
        for(Step step:steps)
            step.resetStep();
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



    public String flowPrintData()
    {
        String data;
        data = "Flow name: " + name + "\n\n";
        data += "Flow description: " + description + "\n\n";
        data += getFormalOutputs() + "\n";
        data += getStrReadOnlyStatus() + "\n";
        data += getStrStepsData() + "\n";
        data += getStrFreeInputs() + "\n";
        data += getStrOutPuts() + "\n";
        System.out.println(data);
        return data;
    }


    public String getFormalOutputs()
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
        //resetFlow();
        flowStatistics.addRunTime(runTime);
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
        String res = "Flows unique ID: " + flowId + "\n";
        res += "Flow name: " + name + "\n";
        res += "Flow's final state : " + state_after_run + "\n";

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
