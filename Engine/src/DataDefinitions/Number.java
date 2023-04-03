package DataDefinitions;

public class Number
{
    private int number;
    private final boolean user_friendly=true;
    private  boolean mandatory;

    public Number(int number, boolean mandatory)
    {
        this.number = number;
        this.mandatory = mandatory;
    }

    public int getNumber() {
        return number;
    }
}
