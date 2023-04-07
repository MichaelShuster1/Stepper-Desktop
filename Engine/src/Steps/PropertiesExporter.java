package Steps;

import DataDefinitions.DataRelation;
import DataDefinitions.DataString;
import DataDefinitions.Input;
import DataDefinitions.Output;

public class PropertiesExporter extends Step{
    public PropertiesExporter(String name) {
        super(name, true);
        DataRelation input = new DataRelation("SOURCE");
        inputs.add(new Input(input,false,true));

        outputs.add(new Output(new DataString("RESULT")));
    }

    @Override
    public void Run() {

    }
}
