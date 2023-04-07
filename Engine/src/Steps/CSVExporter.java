package Steps;

import DataDefinitions.DataRelation;
import DataDefinitions.DataString;
import DataDefinitions.Input;
import DataDefinitions.Output;

public class CSVExporter extends Step
{
    public CSVExporter(String name)
    {
        super(name, true);
        DataRelation dataRelation =new DataRelation("SOURCE");
        inputs.add(new Input(dataRelation,false,true));

        DataString dataString =new DataString("RESULT");
        outputs.add(new Output(dataString));
    }

    @Override
    public void Run() {

    }
}
