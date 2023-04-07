package DataDefinitions;

public class Output
{
    private DataDefinition dataDefinition;

    public Output(DataDefinition dataDefinition)
    {
        this.dataDefinition = dataDefinition;
    }

    public DataDefinition getDataDefinition()
    {
        return dataDefinition;
    }

    public void setDataDefinition(DataDefinition dataDefinition)
    {
        this.dataDefinition = dataDefinition;
    }

    public Object getData()
    {
        return dataDefinition.getData();
    }

    public void setData(Object data)
    {
        dataDefinition.setData(data);
    }
}
