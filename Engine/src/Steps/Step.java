package Steps;

import DataDefinitions.Input;
import DataDefinitions.Output;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Step
{
    protected String name;
    protected boolean read_only;

    protected List<Input> inputs;
    protected Map<String,Integer> nameToInputIndex;
    protected List<Output> outputs;
    protected Map<String,Integer> nameToOutputIndex;

    public Step(String name, boolean read_only)
    {
        this.name = name;
        this.read_only = read_only;
        inputs=new ArrayList<>();
        outputs=new ArrayList<>();
        nameToInputIndex=new HashMap<>();
        nameToOutputIndex=new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public boolean isRead_only() {
        return read_only;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void ChangeInputName(String oldName,String newName)
    {
        Integer index=nameToInputIndex.remove(oldName);
        nameToInputIndex.put(newName,index);
        inputs.get(index).setName(newName);
    }

    public void ChangeOutputName(String oldName,String newName)
    {
        Integer index=nameToOutputIndex.remove(oldName);
        nameToOutputIndex.put(newName,index);
        outputs.get(index).setName(newName);
    }



    public void setRead_only(boolean read_only) {
        this.read_only = read_only;
    }

    public Input getInput(int index)
    {
        return inputs.get(index);
    }

    public Output getOutput(int index)
    {
        return outputs.get(index);
    }

    public List<Input> getInputs()
    {
        return inputs;
    }

    public List<Output> getOutputs()
    {
        return outputs;
    }


    public Map<String, Integer> getNameToInputIndex()
    {
        return nameToInputIndex;
    }

    public Map<String, Integer> getNameToOutputIndex()
    {
        return nameToOutputIndex;
    }

    public abstract void Run();
}
