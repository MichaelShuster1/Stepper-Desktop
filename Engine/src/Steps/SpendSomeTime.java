package Steps;

import DataDefinitions.DataNumber;

public class SpendSomeTime
{
    private DataNumber TIME_TO_SPEND;

    public SpendSomeTime(int number) throws RuntimeException
    {
        if(number<=0)
        {
            throw new RuntimeException("entered non-positive number");
        }
        TIME_TO_SPEND = new DataNumber(number,"DataNumber","INPUT",true);
    }
    public void Sleep()
    {
        try
        {
            Thread.sleep(TIME_TO_SPEND.getNumber()*1000);
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }
}
