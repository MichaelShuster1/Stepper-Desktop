package Steps;

import DataDefinitions.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
        int index = 1;
        List<File> fileList = (List<File>) inputs.get(0).getData();
        String prefix = (String) inputs.get(1).getData();
        String suffix = (String) inputs.get(2).getData();
        String failedToRenameFiles;
        String[]dataTableNames = {"Index","Original name","Name after change"};
        Relation dataTable = new Relation(dataTableNames);
        if(prefix == null)
        {
            prefix = "";
        }
        if(suffix == null)
        {
            suffix = "";
        }

        if(fileList.size() == 0)
        {
            setState_after_run(State.SUCCESS);
            //add logs;
        }
        else
        {
            for(File file:fileList)
            {
                String currName = file.getName();
                String parentPath = file.getParent();
                String newName;
                int extensionDotIndex = currName.lastIndexOf(".");
                newName = prefix + currName.substring(0,extensionDotIndex - 1) +
                          suffix + currName.substring(extensionDotIndex,currName.length()-1);
                File newNameFile = new File(parentPath + newName);
                if(file.renameTo(newNameFile))
                {

                }
            }
        }





    }
}
