package step;

import datadefinition.DataEnumerator;
import datadefinition.DataString;
import datadefinition.Input;
import datadefinition.Output;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Zipper extends Step {

    public Zipper(String name, boolean continueIfFailing) {
        super(name, false, continueIfFailing);
        defaultName = "Zipper";


        DataString dataString = new DataString("SOURCE");
        inputs.add(new Input(dataString, true, true, "Source"));
        nameToInputIndex.put("SOURCE", 0);

        Set<String> values = new HashSet<>();
        values.add("ZIP");
        values.add("UNZIP");
        DataEnumerator dataEnumerator = new DataEnumerator("OPERATION", values);
        inputs.add(new Input(dataEnumerator, true, true, "Operation type"));
        nameToInputIndex.put("OPERATION", 1);

        outputs.add(new Output(new DataString("RESULT"), "Zip operation result"));
        nameToOutputIndex.put("RESULT", 0);
    }

    @Override
    public void run() {
        Long startTime = System.currentTimeMillis();
        String path = (String) inputs.get(0).getData();
        String operation = (String) inputs.get(1).getData();
        String res = "";

        if (!checkGotInputs(2)) {
            outputs.get(0).setData("Failed, not all mandatory inputs received");
            runTime = System.currentTimeMillis() - startTime;
            return;
        }


        switch (operation) {
            case "ZIP":
                //zip();
                break;
            case "UNZIP":
                addLineToLog("About to perform operation " + operation + " on source " + path);
                res = unzip(path);
                break;
        }


        outputs.get(0).setData(res);
        runTime = System.currentTimeMillis() - startTime;
    }


    private String unzip(String path) {
        File file = new File(path);
        String res = "";


        if (file.exists()) {
            if (path.endsWith("zip")) {
                String targetPath = file.getParentFile().getAbsolutePath();
                try (ZipInputStream zipStream = new ZipInputStream(
                        new FileInputStream(path))) {
                    extractZip(zipStream, targetPath);
                } catch (IOException e) {
                    res = processFailure(e.getMessage());
                }
            } else {
                res = processFailure("the given file name does not end with an .zip extension");
            }
        } else {
            res = processFailure("the file does not exist in the given path");
        }

        if(res.equals(""))
        {
            addLineToLog("finished to unzip the given file");
            summaryLine="Step ended successfully, finished to unzip the given file";
            res="SUCCESS";
            stateAfterRun=State.SUCCESS;
        }
        return res;
    }

    private String processFailure(String reason) {
        String res;
        addLineToLog("Failed to unzip: " + reason);
        summaryLine = "Step failed, " + reason;
        res = "Failed, " + reason;
        stateAfterRun = State.FAILURE;
        return res;
    }


    private String extractFile(ZipInputStream zipStream, String path) {
        String res = "";
        byte[] buffer = new byte[1024];
        try (FileOutputStream outputStream = new FileOutputStream(path)) {
            int len;
            while ((len = zipStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
        } catch (IOException e) {
            res = processFailure(e.getMessage());
        }
        return res;
    }


    private String extractZip(ZipInputStream zipStream, String path) {
        String res = "";
        boolean failed = false;

        try {
            ZipEntry entry = zipStream.getNextEntry();

            while (entry != null && !failed) {
                String name = entry.getName();;

                if (entry.isDirectory()) {
                    File newDir = new File(path + File.separator + name);
                    if (!newDir.isDirectory() && !newDir.mkdirs()) {
                        res = processFailure("an error occurred while trying to create a subfolder");
                        failed = true;
                    }
                } else {
                    File parent = new File(path + File.separator + name).getParentFile();

                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        res = processFailure("an error occurred while trying to create a subfolder");
                        failed = true;
                    } else {
                        extractFile(zipStream, path + File.separator + name);
                    }
                }
                zipStream.closeEntry();
                entry = zipStream.getNextEntry();
            }
            zipStream.closeEntry();
        } catch (IOException e) {
            res = processFailure(e.getMessage());
        }
        return res;
    }
}
