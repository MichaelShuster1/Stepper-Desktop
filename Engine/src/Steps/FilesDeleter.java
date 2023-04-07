package Steps;

import DataDefinitions.*;

import java.util.List;


public class FilesDeleter extends Step
{

    public FilesDeleter(String name)
    {
        super(name, false);

        DataList listData = new DataList("FILES_LIST");
        inputs.add(new Input(listData,false,true));
        nameToInputIndex.put("FILES_LIST",0);

        outputs.add(new Output(new DataList("DELETED_LIST")));
        nameToOutputIndex.put("DELETED_LIST",0);
        outputs.add(new Output(new DataMapping("DELETION_STATS")));
        nameToOutputIndex.put("DELETION_STATS",1);
    }


    @Override
    public void Run() {

    }
}

