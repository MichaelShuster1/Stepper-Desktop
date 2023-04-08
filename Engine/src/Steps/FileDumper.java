package Steps;

import DataDefinitions.DataString;
import DataDefinitions.Input;
import DataDefinitions.Output;

public class FileDumper extends Step
{
    public FileDumper(String name)
    {
        super(name, true);
        defaultName = "File Dumper";

        inputs.add(new Input(new DataString("CONTENT"),true,true));
        nameToInputIndex.put("CONTENT",0);

        inputs.add(new Input(new DataString("FILE_NAME"),true,true));
        nameToInputIndex.put("FILE_NAME",1);

        outputs.add(new Output(new DataString("RESULT")));
        nameToOutputIndex.put("RESULT",0);
    }

    @Override
    public void Run() {

    }
}
