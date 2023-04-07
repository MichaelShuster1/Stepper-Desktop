package Steps;

import DataDefinitions.DataFile;
import DataDefinitions.DataList;
import DataDefinitions.DataNumber;

import java.util.List;

public class FilesDeleter extends Step {

    public FilesDeleter(String name, DataList list)
    {
        super(name, false);
        inputs.add(list);
    }


    public void updateOutPuts(DataList list, DataNumber number)
    {
        outputs.add(list);
        outputs.add(number);
    }


}
