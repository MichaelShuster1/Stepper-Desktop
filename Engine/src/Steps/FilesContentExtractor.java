package Steps;

import DataDefinitions.*;

public class FilesContentExtractor extends Step
{
    public FilesContentExtractor(String name)
    {
        super(name,true);
        defaultName = "Files Content Extractor";

        DataList dataList =new DataList("FILES_LIST");
        inputs.add(new Input(dataList,false,true));
        nameToInputIndex.put("FILES_LIST",0);

        DataNumber dataNumber =new DataNumber("LINE");
        inputs.add(new Input(dataNumber,true,true));
        nameToInputIndex.put("LINE",1);

        DataRelation dataRelation =new DataRelation("DATA");
        outputs.add(new Output(dataRelation));
        nameToOutputIndex.put("DATA",0);

    }

    @Override
    public void Run() {

    }
}
