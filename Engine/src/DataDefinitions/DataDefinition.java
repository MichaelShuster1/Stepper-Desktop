package DataDefinitions;

import java.io.Serializable;

public abstract class DataDefinition<T> implements Serializable
{
    protected String name;
    protected final String type;

    public DataDefinition(String name, String type)
    {
        this.name = name;
        this.type = type;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getType()
    {
        return type;
    }

    public abstract void setData(T data);

    public abstract T getData();
}
