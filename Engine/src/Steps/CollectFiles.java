package Steps;


import DataDefinitions.*;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CollectFiles extends Step
{
    public CollectFiles(String name,boolean continue_if_failing)
    {
        super(name,true,continue_if_failing);
        defaultName = "Collect Files In Folder";

        DataString dataString=new DataString("FOLDER_NAME");
        inputs.add(new Input(dataString,true,true));
        nameToInputIndex.put("FOLDER_NAME",0);

        dataString=new DataString("FILTER");
        inputs.add(new Input(dataString,true,false));
        nameToInputIndex.put("FILTER",1);

        outputs.add(new Output(new DataList<File>("FILES_LIST")));
        nameToOutputIndex.put("FILES_LIST",0);

        outputs.add(new Output(new DataNumber("TOTAL_FOUND")));
        nameToOutputIndex.put("TOTAL_FOUND",1);
    }


    @Override
    public void Run() {
        int count = 0;
        String directoryPath = (String) inputs.get(0).getData();
        String filter = (String) inputs.get(1).getData();
        List<File> fileList = new ArrayList<>();
        File directory = new File(directoryPath);

        if(!directory.exists())
        {
            setState_after_run(State.FAILURE);
            //add logs etc;
        } else if (!directory.isDirectory()) {
            setState_after_run(State.FAILURE);
            //add logs etc;
        } else {
            FileFilter toFilter = new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    if(filter != null)
                        return pathname.isFile() && pathname.getName().endsWith(filter);
                    else
                        return pathname.isFile();
                }
            };

            File[] files = directory.listFiles(toFilter);
            count = files.length;
            if(count == 0)
            {
                setState_after_run(State.WARNING);
                //add logs etc;
            }
            else
            {
                setState_after_run(State.SUCCESS);
                fileList.addAll(Arrays.asList(files));
                //add logs etc;
            }

            outputs.get(0).setData(fileList);
            outputs.get(1).setData(count);

        }


    }
}
