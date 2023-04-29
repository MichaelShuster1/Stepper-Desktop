package step;

import datadefinition.DataEnumerator;
import datadefinition.DataString;
import datadefinition.Input;
import datadefinition.Output;

import java.io.*;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Zipper extends  Step{

    public Zipper(String name, boolean continueIfFailing) {
        super(name, false, continueIfFailing);
        defaultName="Zipper";


        DataString dataString = new DataString("SOURCE");
        inputs.add(new Input(dataString, true, true, "Source"));
        nameToInputIndex.put("SOURCE", 0);

        Set<String> values=new HashSet<>();
        values.add("ZIP");
        values.add("UNZIP");
        DataEnumerator dataEnumerator =new DataEnumerator("OPERATION",values);
        inputs.add(new Input(dataEnumerator,true,true,"Operation type"));
        nameToInputIndex.put("OPERATION", 1);

        outputs.add(new Output(new DataString("RESULT"), "Zip operation result"));
        nameToOutputIndex.put("RESULT", 0);
    }

    @Override
    public void run() {
        Long startTime = System.currentTimeMillis();
        String path = (String) inputs.get(0).getData();
        String operation = (String) inputs.get(1).getData();
        String res="";

        if (!checkGotInputs(2)) {
            runTime = System.currentTimeMillis() - startTime;
            return;
        }


        switch (operation)
        {
            case "ZIP":
                zip();
                break;
            case "UNZIP":
                res=unzip(path);
                break;
        }


        outputs.get(0).setData(res);
        runTime = System.currentTimeMillis() - startTime;
    }


    public boolean zip(String srcFolder, String destZipFile,File file) {
            boolean result = false;

            try {
                System.out.println("Program Start zipping the given files");
                /*
                 * send to the zip procedure
                 */
                zipFolder(srcFolder, destZipFile, file);
                result = true;
                System.out.println("Given files are successfully zipped");
            } catch (Exception e) {
                System.out.println("Some Errors happned during the zip process");
            } finally {
                return result;
            }
        }

        /*
         * zip the folders
         */
        private void zipFolder(String srcFolder, String destZipFile,File file) throws Exception {
            ZipOutputStream zip = null;
            FileOutputStream fileWriter = null;
            /*
             * create the output stream to zip file result
             */
            fileWriter = new FileOutputStream(destZipFile);
            zip = new ZipOutputStream(fileWriter);
            /*
             * add the folder to the zip
             */
            if(file.isDirectory())
                addFolderToZip("", srcFolder, zip);
            else
                addFileToZip(file.getName(),srcFolder,zip,false);

            /*
             * close the zip objects
             */
            zip.flush();
            zip.close();
        }

        /*
         * recursively add files to the zip files
         */
        private void addFileToZip(String path, String srcFile, ZipOutputStream zip, boolean flag) throws Exception {
            /*
             * create the file object for inputs
             */
            File folder = new File(srcFile);

            /*
             * if the folder is empty add empty folder to the Zip file
             */
            if (flag == true) {
                zip.putNextEntry(new ZipEntry(path + "/" + folder.getName() + "/"));
            } else { /*
             * if the current name is directory, recursively traverse it
             * to get the files
             */
                if (folder.isDirectory()) {
                    /*
                     * if folder is not empty
                     */
                    addFolderToZip(path, srcFile, zip);
                } else {
                    /*
                     * write the file to the output
                     */
                    byte[] buf = new byte[1024];
                    int len;
                    FileInputStream in = new FileInputStream(srcFile);
                    zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
                    while ((len = in.read(buf)) > 0) {
                        /*
                         * Write the Result
                         */
                        zip.write(buf, 0, len);
                    }
                }
            }
        }

        /*
         * add folder to the zip file
         */
        private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip) throws Exception {
            File folder = new File(srcFolder);

            /*
             * check the empty folder
             */
            if (folder.list().length == 0) {
                System.out.println(folder.getName());
                addFileToZip(path, srcFolder, zip, true);
            } else {
                /*
                 * list the files in the folder
                 */
                for (String fileName : folder.list()) {
                    if (path.equals("")) {
                        addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip, false);
                    } else {
                        addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip, false);
                    }
                }
            }
        }



    private String unzip(String path){
        File file=new File(path);
        String res="";


        if(file.exists())
        {
            if(path.endsWith("zip"))
            {
                String targetPath =file.getParentFile().getAbsolutePath();
                byte[] buffer = new byte[1024];
                try (ZipInputStream zipStream= new ZipInputStream(
                        new FileInputStream(path)))
                {

                    ZipEntry entry=zipStream.getNextEntry();
                    while (entry!=null)
                    {
                        String name=entry.getName();

                        if(entry.isDirectory())
                        {
                            File newDir=new File(targetPath + File.separator + name);
                            if(!newDir.mkdirs())
                            {
                                //error
                                //log
                            }
                        }
                        else
                        {
                            File parent = new File(targetPath + File.separator + name).getParentFile();

                            if(parent.mkdirs())
                            {
                                //error
                                //log
                            }

                            try (FileOutputStream outputStream = new FileOutputStream(targetPath + File.separator + name)) {
                                int len;
                                while ((len = zipStream.read(buffer)) > 0) {
                                    outputStream.write(buffer, 0, len);
                                }
                            }
                            zipStream.closeEntry();
                            entry = zipStream.getNextEntry();
                        }
                    }
                    zipStream.closeEntry();
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }


            }
            else
            {
                //add log
                //add summery line
            }
        }
        else
        {
            //add log
            //add summery line
        }

        return res;
    }
}
