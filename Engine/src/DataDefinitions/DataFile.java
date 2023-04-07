package DataDefinitions;


public class DataFile extends  DataDefinition<String>
{
    private String data;

    public DataFile(String name)
    {
        super(name, "DataFile");
    }

    public String getData()
    {
        return data;
    }

    public void setData(String data)
    {
        this.data = data;
    }

}

