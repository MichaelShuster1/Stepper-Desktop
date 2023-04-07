package Steps;

import DataDefinitions.*;

public class FilesRenamer extends Step
{


    public FilesRenamer(String name)
    {
        super(name, false);

        DataList dataList=new DataList("FILES_TO_RENAME");
        inputs.add(new Input(dataList,false,true));
        nameToInputIndex.put("FILES_TO_REMAKE",0);

        DataString dataString=new DataString("PREFIX");
        inputs.add(new Input(dataString,true,false));
        nameToInputIndex.put("PREFIX",1);

        dataString=new DataString("SUFFIX");
        inputs.add(new Input(dataString,true,false));
        nameToInputIndex.put("SUFFIX",2);

        DataRelation dataRelation = new DataRelation("RENAME_RESULT");
        outputs.add(new Output(dataRelation));
        nameToOutputIndex.put("RENAME_RESULT",0);
    }



    @Override
    public void Run() {

    }
}
