package Steps;

import DataDefinitions.DataDefinition;
import DataDefinitions.DataNumber;
import DataDefinitions.Input;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SpendSomeTime extends Step
{
    public SpendSomeTime(String name,boolean continue_if_failing)
    {
        super(name,true,continue_if_failing);
        defaultName = "Spend some Time";

        DataNumber dataNumber=new DataNumber("TIME_TO_SPEND");
        inputs.add(new Input(dataNumber,true,true));
        nameToInputIndex.put("TIME_TO_SPEND",0);

    }


    @Override
    public void Run()
    {
        Integer sleeping_time=(Integer)inputs.get(0).getData();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        if(sleeping_time<=0)
        {
            state_after_run=State.FAILURE;
            addLineToLog("Failed to Run the step " + getName()
                    + " because the given time is a non-positive number"
                    + " [time: " + formatter.format(new Date()) + "]");
        }
        else
        {
            try
            {
                addLineToLog("About to sleep for " + sleeping_time + " seconds"
                        + " [time: " + formatter.format(new Date()) + "]");
                Thread.sleep(sleeping_time * 1000);
                addLineToLog("Done sleeping [time: " + formatter.format(new Date()) + "]");
            }
            catch (InterruptedException e)
            {
                state_after_run = State.FAILURE;
                addLineToLog("Failed to Run the step " + getName() + " because of an internal problem"
                        + " [time: " + formatter.format(new Date()) + "]");
                throw new RuntimeException(e);
            }
        }
    }
}
