package Steps;

import DataDefinitions.*;

import java.util.List;


public class FilesDeleter extends Step {

    public FilesDeleter(String name)
    {
        super(name, false);
        DataList listData = new DataList("FILES_LIST");
        inputs.add(new Input(listData,false,true));
        listData = new DataList("DELETED_LIST");
        outputs.add(new Output(listData));
        outputs.add(new Output(new DataMapping("DELETION_STATS")));

    }


    @Override
    public void Run() {

    }
}

