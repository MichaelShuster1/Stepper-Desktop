package Steps;


import DataDefinitions.DataString;

public class CollectFiles extends Step
{

    CollectFiles(String name,String folderName,String filter)
    {
        super(name,true);
        inputs.add(new DataString("FOLDER_NAME","INPUT",true,folderName));
        inputs.add(new DataString("FILTER","INPUT",false,filter));
    }

}
