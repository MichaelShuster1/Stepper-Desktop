package Flow;

import DataDefinitions.Input;
import DataDefinitions.Output;
import Steps.Step;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Flow(String name, String description)
    {
        this.name = name;
        this.description = description;
        steps = new ArrayList<>();
        nameToIndex = new HashMap<>();
        initFlowInputs();
        initConnections();
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
                            Integer inputIndex = step1.getNameToOutputIndex().get(output.getName());
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





}
