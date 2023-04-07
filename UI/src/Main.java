import Flow.Flow;
import Steps.*;
import javafx.util.Pair;

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


        Map<Pair<String,String>,Pair<String,String>> customMappingInput = new HashMap<>();
        customMappingInput.put(new Pair<>("Collect Files In Folders","FILES_LIST"), new Pair<>("Files Renamer","FILES_TO_RENAME"));
        customMappingInput.put(new Pair<>("CSV Exporter","CSV_RESULT"), new Pair<>("CSV File Dumper","CONTENT"));
        customMappingInput.put(new Pair<>("Properties Exporter","PROP_RESULT"), new Pair<>("Properties File Dumper","CONTENT"));

        flow.CustomMapping(customMappingInput);
        flow.AutomaticMapping();



    }
}
