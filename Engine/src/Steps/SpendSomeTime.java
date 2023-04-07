package Steps;

import DataDefinitions.DataDefinition;
import DataDefinitions.DataNumber;
import DataDefinitions.Input;

public class SpendSomeTime extends Step
{
    public SpendSomeTime(String name) throws RuntimeException
    {
        super(name,true);
        DataNumber dataNumber=new DataNumber("TIME_TO_SPEND",0);
        inputs.add(new Input(dataNumber,true,true));

    }

    @Override
    public void Run()
    {
        try
        {
            Thread.sleep((Integer)inputs.get(0).getData() * 1000);
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }
}
