package Steps;

import DataDefinitions.*;

import java.io.File;

public class FilesContentExtractor extends Step
{
    public FilesContentExtractor(String name,boolean continue_if_failing)
    {
        super(name,true,continue_if_failing);
        defaultName = "Files Content Extractor";

        DataList<File> dataList =new DataList("FILES_LIST");
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
