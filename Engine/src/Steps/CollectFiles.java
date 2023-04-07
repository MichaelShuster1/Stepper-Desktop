package Steps;


import DataDefinitions.*;

public class CollectFiles extends Step
{
    public CollectFiles(String name)
    {
        super(name,true);

        DataString dataString=new DataString("FOLDER_NAME");
        inputs.add(new Input(dataString,true,true));

        dataString=new DataString("FILTER");
        inputs.add(new Input(dataString,true,false));

        outputs.add(new Output(new DataList("FILES_LIST")));
        outputs.add(new Output(new DataNumber("TOTAL_FOUND")));
    }


    @Override
    public void Run() {

    }
}
