package DataDefinitions;


public class DataNumber extends DataDefinition
{
    private Integer number;

    public DataNumber(int number,String name,String typeStream ,boolean mandatory)
    {
        super("DataNumber","DataNumber",typeStream,mandatory,true);
        this.number = number;
        this.mandatory = mandatory;
    }

    public int getNumber()
    {
        return number;
    }

    @Override
    public String toString()
    {
        return number.toString();
    }
}
