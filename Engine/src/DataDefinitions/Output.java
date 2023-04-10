package DataDefinitions;

public class Output
{
    private DataDefinition dataDefinition;
    private String userString;

    public Output(DataDefinition dataDefinition, String userString)
    {
        this.dataDefinition = dataDefinition;
        this.userString = userString;
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

    public void setName(String name)
    {
        dataDefinition.setName(name);
    }

    public String getName()
    {
        return  dataDefinition.getName();
    }

    public String getType()
    {
        return  dataDefinition.getType();
    }

    public String getUserString() {
        return userString;
    }
}
