import DataDefinitions.Input;
import Steps.*;
import javafx.util.Pair;
import sun.java2d.loops.CustomComponent;

import java.util.*;

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
        steps.add(new FilesRenamer(("S3")));
        steps.add(new CSVExporter("S4"));

        Input input=steps.get(0).getInput(0);
        input.setData(number);
        System.out.println("Start of delay: "+ new Date());
        steps.get(0).Run();
        System.out.println("End of delay: "+ new Date());


        List<List<List<Pair<Integer,Integer>>>> connections =new ArrayList<>();
    }
}
