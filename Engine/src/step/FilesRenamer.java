package step;

import datadefinition.*;

import java.io.File;
import java.util.*;

public class FilesRenamer extends Step {


    public FilesRenamer(String name, boolean continue_if_failing) {
        super(name, false, continue_if_failing);
        defaultName = "Files Renamer";

        DataList<File> dataList = new DataList("FILES_TO_RENAME");
        inputs.add(new Input(dataList, false, true, "Files to rename"));
        nameToInputIndex.put("FILES_TO_RENAME", 0);

        DataString dataString = new DataString("PREFIX");
        inputs.add(new Input(dataString, true, false, "Add this prefix"));
        nameToInputIndex.put("PREFIX", 1);

        dataString = new DataString("SUFFIX");
        inputs.add(new Input(dataString, true, false, "Append this suffix"));
        nameToInputIndex.put("SUFFIX", 2);

        DataRelation dataRelation = new DataRelation("RENAME_RESULT");
        outputs.add(new Output(dataRelation, "Rename operation summary"));
        nameToOutputIndex.put("RENAME_RESULT", 0);
    }


    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        int index = 1;
        List<File> fileList = (List<File>) inputs.get(0).getData();
        String prefix = (String) inputs.get(1).getData();
        String suffix = (String) inputs.get(2).getData();
        String failedToRenameFiles = "";
        String[] dataTableNames = {"Index", "Original name", "Name after change"};
        Relation dataTable = new Relation(dataTableNames);

        if (!checkGotInputs(1)) {
            runTime = System.currentTimeMillis() - startTime;
            return;
        }

        String log = "About to start rename " + fileList.size() + " files. Adding prefix: ";
        if (prefix != null)
            log += prefix + " adding suffix: ";
        else
            log += "no prefix adding suffix: ";
        if (suffix != null)
            log += suffix;
        else
            log += "no suffix";


        addLineToLog(log);

        if (fileList.size() == 0) {
            setStateAfterRun(State.SUCCESS);
            summaryLine = "Step ended successfully, the list of files to rename was empty";
        } else {
            for (File file : fileList) {
                String currName = file.getName();
                String parentPath = file.getParent();
                String newName = "";
                int extensionDotIndex = currName.lastIndexOf(".");
                if (prefix != null)
                    newName = prefix;
                if(extensionDotIndex != -1)
                      newName += currName.substring(0, extensionDotIndex);
                else
                    newName += currName;
                if (suffix != null)
                    newName += suffix;
                if(extensionDotIndex != -1)
                       newName += currName.substring(extensionDotIndex, currName.length());
                File newNameFile = new File(parentPath + "\\" + newName);
                if (file.renameTo(newNameFile)) {
                    Map<String, String> row = new HashMap<>();
                    row.put("Index", Integer.toString(index));
                    row.put("Original name", currName);
                    row.put("Name after change", newNameFile.getName());
                    dataTable.addRow(row);
                    index++;
                } else {
                    addLineToLog("Problem renaming file " + currName);
                    if (failedToRenameFiles.length() == 0) {
                        failedToRenameFiles = currName;
                        setStateAfterRun(State.WARNING);
                    } else
                        failedToRenameFiles = currName + ", " + failedToRenameFiles;
                }
            }


        }

        if (failedToRenameFiles.length() > 0) {
            failedToRenameFiles = "Renaming failed for the following files: " + failedToRenameFiles;
        }
        if (getStateAfterRun() != null && getStateAfterRun() == State.WARNING)
            summaryLine = "Warning: " + failedToRenameFiles + "\n" + "Other files(if any) were renamed successfully";

        if (summaryLine == null)
            summaryLine = "Step ended successfully, renamed the files successfully";

        if (stateAfterRun == null)
            stateAfterRun = State.SUCCESS;

        outputs.get(0).setData(dataTable);
        runTime = System.currentTimeMillis() - startTime;

    }
}
