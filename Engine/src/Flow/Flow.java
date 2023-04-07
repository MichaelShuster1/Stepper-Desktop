package Flow;

import Steps.Step;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Flow
{
    private String name;
    private String description;
    private boolean read_only;
    private List<String> formal_outputs;
    private List<Step> steps;
    private List<List<List<Pair<Integer,Integer>>>> connections;

    public Flow(String name, String description, boolean read_only)
    {
        this.name = name;
        this.description = description;
        this.read_only = read_only;
    }

    public void AddStep(Step step)
    {
        steps.add(step);
    }
    public void CustomMapping()
    {

    }

    public void AutomaticMapping()
    {

    }

    public Step getStep(int index)
    {
        return steps.get(index);
    }

    public void RunFlow()
    {

    }


}
