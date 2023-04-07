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


    public Flow(String name, String description)
    {
        this.name = name;
        this.description = description;
        steps = new ArrayList<>();
        connections = new ArrayList<>();
        nameToIndex = new HashMap<>();
    }

    public void AddStep(Step step)
    {
        steps.add(step);
        nameToIndex.put(step.getName(),steps.size()-1);
    }

    public void CustomMapping(Map<Pair<String,String>,Pair<String,String>> customMapping)
    {
        

    }


    public void AutomaticMapping()
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


}
