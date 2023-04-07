import DataDefinitions.Input;
import Steps.CollectFiles;
import Steps.SpendSomeTime;
import Steps.Step;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Main
{
    public static void main(String[] args)
    {
        int number;
        Scanner inputStream=new Scanner(System.in);
        System.out.println("please enter number of seconds to sleep: ");
        number=inputStream.nextInt();

        List<Step> steps=new ArrayList<>();
        steps.add(new SpendSomeTime("s1"));
        steps.add(new CollectFiles("S2"));

        Input input1=steps.get(0).getInput(0);
        input1.setData(number);
        System.out.println("Start of delay: "+ new Date());
        steps.get(0).Run();
        System.out.println("End of delay: "+ new Date());

    }
}
