package Steps;

import DataDefinitions.DataNumber;

public class SpendSomeTime extends Step
{
    public SpendSomeTime(String name,int number) throws RuntimeException
    {
        super(name,true);
        if(number<=0)
        {
            throw new RuntimeException("entered non-positive number");
        }
        inputs.add(new DataNumber(number,"DataNumber","INPUT",true));
    }


    public void Sleep()
    {
        try
        {
            Thread.sleep(((DataNumber)inputs.get(0)).getNumber() * 1000L);
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }
}
