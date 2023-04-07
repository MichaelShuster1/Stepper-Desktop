package Steps;

import DataDefinitions.*;

public class FilesContentExtractor extends Step
{
    public FilesContentExtractor(String name)
    {
        super(name,true);

        DataList dataList =new DataList("FILES_LIST");
        inputs.add(new Input(dataList,false,true));

        DataNumber dataNumber =new DataNumber("LINE");
        inputs.add(new Input(dataNumber,true,true));

        DataRelation dataRelation =new DataRelation("DATA");
        outputs.add(new Output(dataRelation));

    }

    @Override
    public void Run() {

    }
}
