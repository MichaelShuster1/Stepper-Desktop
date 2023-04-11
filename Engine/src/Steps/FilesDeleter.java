package Steps;

import DataDefinitions.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;


public class FilesDeleter extends Step
{

    public FilesDeleter(String name,boolean continue_if_failing)
    {
        super(name, false,continue_if_failing);
        defaultName = "Files Deleter";

        DataList<File> listData = new DataList("FILES_LIST");
        inputs.add(new Input(listData,false,true,"Files to delete:"));
        nameToInputIndex.put("FILES_LIST",0);

        outputs.add(new Output(new DataList<String>("DELETED_LIST"),"Files failed to be deleted:"));
        nameToOutputIndex.put("DELETED_LIST",0);
        outputs.add(new Output(new DataMapping<Integer>("DELETION_STATS"),"Deletion summary result:"));
        nameToOutputIndex.put("DELETION_STATS",1);
    }


    @Override
    public void Run()
    {
        long startTime=System.currentTimeMillis();
        List<File> files =(List<File>)inputs.get(0).getData();
        List<String> paths= new ArrayList<>();
        Map<String,Integer> mapping=new HashMap<>();
        int files_deleted=0,files_not_deleted=0;
        boolean deleted=false;

        if(files.size()!=0)
        {
            addLineToLog("About to start delete"+ files.size() + "files");
            for (File file : files)
            {
                deleted = file.delete();
                if (!deleted)
                {
                    paths.add(file.getAbsolutePath());
                    files_not_deleted++;
                    addLineToLog("Failed to delete file " + file.getAbsolutePath());
                }
                else
                    files_deleted++;
            }

            if (paths.size() == 0)
            {
                state_after_run = State.SUCCESS;
                addLineToLog("All files have been deleted successfully");
                summaryLine="All files have been deleted successfully";
            }
            else
            {
                state_after_run = State.WARNING;
                addLineToLog("Not all files were deleted successfully");
                summaryLine="Not all files were deleted successfully";
            }
        }
        else
        {
            state_after_run = State.SUCCESS;
            addLineToLog("No files to delete were given");
            summaryLine="No files to delete were given";
        }
        outputs.get(0).setData(paths);
        mapping.put("car",files_deleted);
        mapping.put("cdr",files_not_deleted);
        outputs.get(1).setData(mapping);
        runTime=System.currentTimeMillis()-startTime;
        stepStatistics.addRunTime(runTime);
    }
}

