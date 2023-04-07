import DataDefinitions.Input;
import DataDefinitions.Output;
import Flow.Flow;
import Steps.*;
import javafx.util.Pair;
import sun.java2d.loops.CustomComponent;

import java.util.*;

public class Main
{
    public static void main(String[] args)
    {
        Scanner inputStream=new Scanner(System.in);


        Flow flow=new Flow("Rename Files","Given a folder adds prefix and\\or" +
                " suffix to each file name. The renaming results are expressed via CSV and Properties files");

        /*
        flow.AddStep(new CollectFiles("Collect Files In Folder"));
        flow.AddStep(new SpendSomeTime("Spend Some Time"));
        flow.AddStep(new FilesDeleter("Files Deleter"));
        */


        flow.AddStep(new CollectFiles("Collect Files In Folder"));
        flow.AddStep(new FilesRenamer("Files Renamer"));
        flow.AddStep(new CSVExporter("CSV Exporter"));
        flow.AddStep(new FileDumper("CSV File Dumper"));
        flow.AddStep(new PropertiesExporter( "Properties Exporter"));
        flow.AddStep(new FileDumper("Properties File Dumper"));

        flow.getStep(1).getOutput(0).setName("SOURCE");
        flow.getStep(2).getOutput(0).setName("CSV_RESULT");
        flow.getStep(3).getInput(1).setName("CSV_FILE_NAME");
        flow.getStep(3).getOutput(0).setName("CSV_FILE_DUMP_RESULT");
        flow.getStep(4).getOutput(0).setName("PROP_RESULT");
        flow.getStep(5).getInput(1).setName("PROP_FILE_NAME");
        flow.getStep(5).getOutput(0).setName("PROP_FILE_DUMP_RESULT");


        flow.AutomaticMapping();
        /*
        System.out.println("please enter number of seconds to sleep: ");
        number=inputStream.nextInt();
        Input input=steps.get(0).getInput(0);
        input.setData(number);
        System.out.println("Start of delay: "+ new Date());
        steps.get(0).Run();
        System.out.println("End of delay: "+ new Date());
        */
    }
}
