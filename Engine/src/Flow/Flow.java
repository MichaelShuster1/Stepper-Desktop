package Flow;

import DataDefinitions.Input;
import DataDefinitions.Output;
import Steps.Step;
import javafx.util.Pair;

import java.util.*;

public class Flow
{
    private String name;
    private String description;
    private boolean read_only;
    private List<String> formal_outputs;
    private List<Step> steps;
    private Map<String,Integer> nameToIndex;
    private List<List<List<Pair<Integer,Integer>>>> connections;
    private Map<String,List<Integer>> flowInputs;
    private Map<String,List<Integer>> flowFreeInputs;
    private Map<String,Boolean> freeInputsIsReq;

    public Flow(String name, String description)
    {
        this.name = name;
        this.description = description;
        steps = new ArrayList<>();
        nameToIndex = new HashMap<>();
        formal_outputs = new ArrayList<>();
    }

    public void AddStep(Step step)
    {
        steps.add(step);
        nameToIndex.put(step.getName(),steps.size()-1);
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


    /*  public void AutomaticMapping()
    {
        int b;
        for(int i=0;i<steps.size();i++)
        {
            Step step = steps.get(i);
            System.out.println("in step: "+step.getName());
            List<Output> outputs =step.getOutputs();
            List<List<Pair<Integer,Integer>> > list=new ArrayList<>();
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
                                && input.getType().equals(output.getType()))
                        {
                            pairs.add(new Pair<>(j,b));
                            System.out.println(step.getName()+": "+input.getName());
                        }
                        b++;
                    }
                }
                list.add(pairs);
            }
            connections.add(list);
        }
    }
*/

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
        int a;
        for(int i=0;i<steps.size();i++)
        {
            Step step = steps.get(i);
            System.out.println("in step: "+step.getName());
            List<Output> outputs =step.getOutputs();
            List<List<Pair<Integer,Integer>> > list=new ArrayList<>();
            a=0;
            for(Output output:outputs)
            {
                System.out.println("the output "+ output.getName()+ " connects to the following inputs");
                List<Pair<Integer,Integer>> pairs=new ArrayList<>();
                List<Integer> integerList =flowInputs.get(output.getName());
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
        flowFreeInputs = new LinkedHashMap<>();
        freeInputsIsReq =new HashMap<>();
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


    public String getInputMenu()
    {
        int i=1;
        String inputMenu="",mandatory;
        for (String inputName:flowFreeInputs.keySet())
        {
            Integer inputIndex,stepIndex=flowFreeInputs.get(inputName).get(0);
            Step step= steps.get(stepIndex);
            inputIndex=step.getNameToInputIndex().get(inputName);
            if(step.getInput(inputIndex).isMandatory())
                mandatory="mandatory";
            else
                mandatory="optional";

            inputMenu+= i + "." + inputName+ "["+ mandatory+ "]\n";
            i++;
        }
        return inputMenu;
    }



    public Step getStep(int index)
    {
        return steps.get(index);
    }

    public void RunFlow()
    {
        int i=0;
        for (Step step:steps)
        {
            step.Run();

            /*
            Input input=steps.get("name of step").getInput("name of input");
            input.setData(step.getOuput("name of output").getData());
            */

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
            for (String currOutput: formal_outputs) {
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
                if (input.isMandatory())
                    currInput += "This input is mandatory: No\n\n";
                else
                    currInput += "This input is mandatory: Yes\n\n";
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








}
