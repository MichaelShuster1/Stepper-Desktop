package Steps;

import DataDefinitions.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class FilesRenamer extends Step
{


    public FilesRenamer(String name,boolean continue_if_failing)
    {
        super(name, false,continue_if_failing);
        defaultName = "Files Renamer";

        DataList<File> dataList=new DataList("FILES_TO_RENAME");
        inputs.add(new Input(dataList,false,true,"Files to rename:"));
        nameToInputIndex.put("FILES_TO_RENAME",0);

        DataString dataString=new DataString("PREFIX");
        inputs.add(new Input(dataString,true,false,"Add this prefix:"));
        nameToInputIndex.put("PREFIX",1);

        dataString=new DataString("SUFFIX");
        inputs.add(new Input(dataString,true,false,"Append this suffix"));
        nameToInputIndex.put("SUFFIX",2);

        DataRelation dataRelation = new DataRelation("RENAME_RESULT");
        outputs.add(new Output(dataRelation,"Rename operation summary"));
        nameToOutputIndex.put("RENAME_RESULT",0);
    }



    @Override
    public void Run()
    {
        long startTime=System.currentTimeMillis();
        int index = 1;
        List<File> fileList = (List<File>) inputs.get(0).getData();
        String prefix = (String) inputs.get(1).getData();
        String suffix = (String) inputs.get(2).getData();
        String failedToRenameFiles = "";
        String[]dataTableNames = {"Index","Original name","Name after change"};
        Relation dataTable = new Relation(dataTableNames);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.format(new Date());


        addLineToLog("About to start rename " + fileList.size() + " files. Adding prefix: " + prefix + " adding suffix: " + suffix
                + " [time: " + formatter.format(new Date()) + "]");

        if(fileList.size() == 0)
        {
            setState_after_run(State.SUCCESS);
            summaryLine = "The list of files to rename was empty, the step ended successfully";
        }
        else
        {
            for(File file:fileList)
            {
                String currName = file.getName();
                String parentPath = file.getParent();
                String newName = "";
                int extensionDotIndex = currName.lastIndexOf(".");
                if(prefix != null)
                    newName = prefix;
                newName += currName.substring(0,extensionDotIndex);
                if(suffix != null)
                       newName += suffix;
                newName += currName.substring(extensionDotIndex,currName.length());
                File newNameFile = new File(parentPath + "\\" + newName);
                if(file.renameTo(newNameFile))
                {
                    Map<String, String > row = new HashMap<>();
                    row.put("Index",Integer.toString(index));
                    row.put("Original name",currName);
                    row.put("Name after change",newNameFile.getName());
                    dataTable.addRow(row);
                    setState_after_run(State.SUCCESS);
                    index++;
                }
                else {
                    addLineToLog("Problem renaming file " + currName
                            + " [time: " + formatter.format(new Date()) + "]");
                    if(getState_after_run() != null && getState_after_run() == State.WARNING)
                        failedToRenameFiles = currName;
                    else {
                        failedToRenameFiles = currName + ", " + failedToRenameFiles;
                        setState_after_run(State.WARNING);
                    }
                }
                failedToRenameFiles = "Renaming failed for the following files: " + failedToRenameFiles;
                if(getState_after_run() != null && getState_after_run() == State.WARNING)
                    summaryLine = "Warning: " + failedToRenameFiles + "\n" + "Other files(if any) were renamed successfully";

            }


        }
        outputs.get(0).setData(dataTable);
        runTime=System.currentTimeMillis()-startTime;
        stepStatistics.addRunTime(runTime);
    }
}
