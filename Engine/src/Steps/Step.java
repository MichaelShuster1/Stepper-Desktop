package Steps;

import DataDefinitions.DataDefinition;

import java.util.ArrayList;
import java.util.List;

public abstract class Step
{
    protected String name;
    protected boolean read_only;
    protected List<DataDefinition> inputs;
    protected List<DataDefinition> outputs;

    public Step(String name, boolean read_only)
    {
        this.name = name;
        this.read_only = read_only;
        inputs=new ArrayList<>();
        outputs=new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public boolean isRead_only() {
        return read_only;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRead_only(boolean read_only) {
        this.read_only = read_only;
    }
}
