package DataDefinitions;


public class DataNumber extends DataDefinition<Integer>
{
    private Integer data;

    public DataNumber(String name,Integer number)
    {
        super("DataNumber","DataNumber");
        this.data = number;
    }

    @Override
    public String toString()
    {
        return data.toString();
    }

    @Override
    public void setData(Integer data)
    {
        this.data=data;
    }

    @Override
    public Integer getData()
    {
        return data;
    }
}
