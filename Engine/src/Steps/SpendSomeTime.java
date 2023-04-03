package Steps;

import DataDefinitions.Number;

public class SpendSomeTime
{
    private Number TIME_TO_SPEND;




    public SpendSomeTime(int number) throws RuntimeException
    {
        if(number<=0)
        {
            throw new RuntimeException("entered non-positive number");
        }
        TIME_TO_SPEND = new Number(number,true);
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
