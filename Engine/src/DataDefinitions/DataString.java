package DataDefinitions;

public class DataString extends DataDefinition<String>
{
    private String data;

    public DataString(String name)
    {
        super(name, "String");
    }

    @Override
    public String getData()
    {
        return data;
    }

    public void setData(String data)
    {
        this.data = data;
    }

    @Override
    public String toString()
    {
        return data;
    }
}
