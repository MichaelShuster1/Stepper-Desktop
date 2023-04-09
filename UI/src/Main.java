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


        flow.AddStep(new CollectFiles("Collect Files In Folder",false));
        flow.AddStep(new FilesRenamer("Files Renamer",false));
        flow.AddStep(new CSVExporter("CSV Exporter",false));
        flow.AddStep(new FileDumper("CSV File Dumper",true));
        flow.AddStep(new PropertiesExporter( "Properties Exporter",false));
        flow.AddStep(new FileDumper("Properties File Dumper",true));


        flow.getStep(1).ChangeOutputName("RENAME_RESULT","SOURCE");
        flow.getStep(2).ChangeOutputName("RESULT","CSV_RESULT");
        flow.getStep(3).ChangeInputName("FILE_NAME","CSV_FILE_NAME");
        flow.getStep(3).ChangeOutputName("RESULT","CSV_FILE_DUMP_RESULT");
        flow.getStep(4).ChangeOutputName("RESULT","PROP_RESULT");
        flow.getStep(5).ChangeInputName("FILE_NAME","PROP_FILE_NAME");
        flow.getStep(5).ChangeOutputName("RESULT","PROP_FILE_DUMP_RESULT");


        Map<Pair<String,String>,Pair<String,String>> customMappingInput = new HashMap<>();
        customMappingInput.put(new Pair<>("Collect Files In Folder","FILES_LIST"), new Pair<>("Files Renamer","FILES_TO_RENAME"));
        customMappingInput.put(new Pair<>("CSV Exporter","CSV_RESULT"), new Pair<>("CSV File Dumper","CONTENT"));
        customMappingInput.put(new Pair<>("Properties Exporter","PROP_RESULT"), new Pair<>("Properties File Dumper","CONTENT"));



        flow.CustomMapping(customMappingInput);
        flow.AutomaticMapping();
        flow.CalculateFreeInputs();
        flow.flowPrintData();

        Step collectFiles = new CollectFiles("Collect files in folder",false);
        collectFiles.getInput(0).setData("C:\\Users\\Igal\\Desktop\\New folder");
        //collectFiles.getInput(1).setData();
        collectFiles.Run();

    }
}
