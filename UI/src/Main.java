import DataDefinitions.DataDefinition;
import DataDefinitions.DataRelation;
import DataDefinitions.DataString;
import Steps.SpendSomeTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Main
{
    public static void main(String[] args)
    {
        int number;
        Scanner input=new Scanner(System.in);
        System.out.println("please enter number of seconds to sleep: ");
        number=input.nextInt();
        SpendSomeTime s1 =new SpendSomeTime(number);
        System.out.println("Start of delay: "+ new Date());
        s1.Sleep();
        System.out.println("End of delay: "+ new Date());


    }
}
