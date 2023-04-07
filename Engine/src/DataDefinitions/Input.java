package DataDefinitions;

public class Input
{
    private DataDefinition dataDefinition;
    private final boolean user_friendly;
    private  final boolean mandatory;

    public Input(DataDefinition dataDefinition, boolean user_friendly, boolean mandatory)
    {
        this.dataDefinition = dataDefinition;
        this.user_friendly = user_friendly;
        this.mandatory = mandatory;
    }

    public DataDefinition getDataDefinition()
    {
        return dataDefinition;
    }


    public Object getData()
    {
        return dataDefinition.getData();
    }

    public void setData(Object data)
    {
        dataDefinition.setData(data);
    }

    public void setDataDefinition(DataDefinition dataDefinition)
    {
        this.dataDefinition = dataDefinition;
    }
}
