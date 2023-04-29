package step;


import datadefinition.*;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class CollectFiles extends Step {
    public CollectFiles(String name, boolean continue_if_failing) {
        super(name, true, continue_if_failing);
        defaultName = "Collect Files In Folder";

        DataString dataString = new DataString("FOLDER_NAME");
        inputs.add(new Input(dataString, true, true, "Folder name to scan"));
        nameToInputIndex.put("FOLDER_NAME", 0);

        dataString = new DataString("FILTER");
        inputs.add(new Input(dataString, true, false, "Filter only these files"));
        nameToInputIndex.put("FILTER", 1);

        outputs.add(new Output(new DataList<File>("FILES_LIST"), "Files list"));
        nameToOutputIndex.put("FILES_LIST", 0);

        outputs.add(new Output(new DataNumber("TOTAL_FOUND"), "Total files found"));
        nameToOutputIndex.put("TOTAL_FOUND", 1);
    }


    @Override
    public void run() {
        Long startTime = System.currentTimeMillis();
        int count = 0;
        String directoryPath = (String) inputs.get(0).getData();
        String filter = (String) inputs.get(1).getData();
        List<File> fileList = new ArrayList<>();
        File directory = new File(directoryPath);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.format(new Date());

        if (!checkGotInputs(1)) {
            runTime = System.currentTimeMillis() - startTime;
            return;
        }


        String log = "Reading folder " + directory.getAbsolutePath() + " content with filter: ";
        if (filter != null)
            log += filter;
        else
            log += "no filter was provided";
        addLineToLog(log);


        if (!directory.exists()) {
            setStateAfterRun(State.FAILURE);
            addLineToLog("Accessing path  " + directoryPath + " has failed");
            summaryLine = "Step failed, the path provided was not found";
        } else if (!directory.isDirectory()) {
            setStateAfterRun(State.FAILURE);
            addLineToLog("Found the path " + directoryPath + ", but its not a folder");
            summaryLine = "Step failed, the path provided was not a folder(directory)";
        } else {
            FileFilter toFilter = new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    if (filter != null)
                        return pathname.isFile() && pathname.getName().endsWith(filter);
                    else
                        return pathname.isFile();
                }
            };

            File[] files = directory.listFiles(toFilter);
            count = files.length;

            addLineToLog("Found " + count + " files in folder matching the filter");

            if (count == 0) {
                setStateAfterRun(State.WARNING);
                summaryLine = "Warning: no matching files found in the provided folder";
            } else {
                setStateAfterRun(State.SUCCESS);
                fileList.addAll(Arrays.asList(files));
                summaryLine = "Step ended successfully," + count + " files were collected to the list";
            }
        }
        outputs.get(0).setData(fileList);
        outputs.get(1).setData(count);
        runTime = System.currentTimeMillis() - startTime;
    }
}
