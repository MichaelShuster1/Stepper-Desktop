package Steps;


import DataDefinitions.*;

public class CollectFiles extends Step
{
    public CollectFiles(String name)
    {
        super(name,true);

        DataString dataString=new DataString("FOLDER_NAME");
        inputs.add(new Input(dataString,true,true));
        nameToInputIndex.put("FOLDER_NAME",0);

        dataString=new DataString("FILTER");
        inputs.add(new Input(dataString,true,false));
        nameToInputIndex.put("FILTER",0);

        outputs.add(new Output(new DataList("FILES_LIST")));
        nameToOutputIndex.put("FILES_LIST",0);

        outputs.add(new Output(new DataNumber("TOTAL_FOUND")));
        nameToOutputIndex.put("TOTAL_FOUND",1);
    }


    @Override
    public void Run() {

    }
}
