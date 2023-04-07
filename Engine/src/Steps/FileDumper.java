package Steps;

import DataDefinitions.DataString;
import DataDefinitions.Input;
import DataDefinitions.Output;

public class FileDumper extends Step{
    public FileDumper(String name) {
        super(name, true);
        inputs.add(new Input(new DataString("CONTENT"),true,true));
        inputs.add(new Input(new DataString("FILE_NAME"),true,true));

        outputs.add(new Output(new DataString("RESULT")));

    }

    @Override
    public void Run() {

    }
}
