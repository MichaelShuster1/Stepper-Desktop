package Steps;


import DataDefinitions.DataString;
import DataDefinitions.Input;

public class CollectFiles extends Step
{
    public CollectFiles()
    {
        super("CollectFiles",true);
        DataString dataString=new DataString("FOLDER_NAME");
        inputs.add(new Input(dataString,true,true));
        dataString=new DataString("FILTER");
        inputs.add(new Input(dataString,true,false));
    }
    public CollectFiles(String name)
    {
        super(name,true);
        DataString dataString=new DataString("FOLDER_NAME");
        inputs.add(new Input(dataString,true,true));
        dataString=new DataString("FILTER");
        inputs.add(new Input(dataString,true,false));
    }

    @Override
    public void Run() {

    }
}
