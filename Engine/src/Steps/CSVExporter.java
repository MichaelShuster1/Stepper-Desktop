package Steps;

import DataDefinitions.DataRelation;
import DataDefinitions.DataString;
import DataDefinitions.Input;
import DataDefinitions.Output;

public class CSVExporter extends Step
{
    public CSVExporter(String name,boolean continue_if_failing)
    {
        super(name, true,continue_if_failing);
        defaultName = "CSV Exporter";

        DataRelation dataRelation =new DataRelation("SOURCE");
        inputs.add(new Input(dataRelation,false,true));
        nameToInputIndex.put("SOURCE",0);

        DataString dataString =new DataString("RESULT");
        outputs.add(new Output(dataString));
        nameToOutputIndex.put("RESULT",0);
    }

    @Override
    public void Run()
    {



    }
}
