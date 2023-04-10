package Steps;

import DataDefinitions.*;

import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CSVExporter extends Step
{
    public CSVExporter(String name,boolean continue_if_failing)
    {
        super(name, true,continue_if_failing);
        defaultName = "CSV Exporter";

        DataRelation dataRelation =new DataRelation("SOURCE");
        inputs.add(new Input(dataRelation,false,true,"Source data:"));
        nameToInputIndex.put("SOURCE",0);

        DataString dataString =new DataString("RESULT");
        outputs.add(new Output(dataString,"CSV export result:"));
        nameToOutputIndex.put("RESULT",0);
    }

    @Override
    public void Run()
    {
        String res = "";
        Relation dataTable = (Relation) inputs.get(0).getData();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.format(new Date());
        List<Map<String,String>> rows = dataTable.getRows();


        addLineToLog("About to process " + (rows.size()) + " lines of data"
                + " [time: " + formatter.format(new Date()) + "]");

        if(rows.size() == 0)
        {
            setState_after_run(State.WARNING);
            summaryLine = "Warning: the data provided was empty, the CSV format created contains the columns names only";

        }
        else {
            setState_after_run(State.SUCCESS);
            summaryLine = "Step ended successfully, CSV format created.";
        }
        for(String name: dataTable.getColumnNames())
        {
            res += name + ", ";
        }
        res = res.substring(0,res.length()-2) +"\r\n";


        for(int i = 0; i< rows.size();i++)
        {
            Map<String,String> currRow = rows.get(i);
            for(int j = 0; j < currRow.size();j++)
            {
                res += currRow.get(dataTable.getColumnNames().get(j)) + ", ";
            }
            res = res.substring(0,res.length()-2) +"\r\n";
        }


        outputs.get(0).setData(res);

    }
}
