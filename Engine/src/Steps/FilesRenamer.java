package Steps;

import DataDefinitions.*;

import java.io.File;

public class FilesRenamer extends Step
{


    public FilesRenamer(String name,boolean continue_if_failing)
    {
        super(name, false,continue_if_failing);
        defaultName = "Files Renamer";

        DataList<File> dataList=new DataList("FILES_TO_RENAME");
        inputs.add(new Input(dataList,false,true));
        nameToInputIndex.put("FILES_TO_RENAME",0);

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
