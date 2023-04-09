package Steps;

import DataDefinitions.*;

import java.io.File;
import java.util.List;


public class FilesDeleter extends Step
{

    public FilesDeleter(String name,boolean continue_if_failing)
    {
        super(name, false,continue_if_failing);
        defaultName = "Files Deleter";

        DataList<File> listData = new DataList("FILES_LIST");
        inputs.add(new Input(listData,false,true));
        nameToInputIndex.put("FILES_LIST",0);

        outputs.add(new Output(new DataList<String>("DELETED_LIST")));
        nameToOutputIndex.put("DELETED_LIST",0);
        outputs.add(new Output(new DataMapping("DELETION_STATS")));
        nameToOutputIndex.put("DELETION_STATS",1);
    }


    @Override
    public void Run() {

    }
}

