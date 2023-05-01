package step;

import datadefinition.DataString;
import datadefinition.Input;
import datadefinition.Output;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandLine extends Step{
    public CommandLine(String name, boolean continueIfFailing) {
        super(name, false, continueIfFailing);
        defaultName="CommandLine";

        DataString dataString = new DataString("COMMAND");
        inputs.add(new Input(dataString, true, true, "Command"));
        nameToInputIndex.put("COMMAND", 0);

        dataString = new DataString("ARGUMENTS");
        inputs.add(new Input(dataString, true, false, "Command arguments"));
        nameToInputIndex.put("ARGUMENTS", 1);

        outputs.add(new Output(new DataString("RESULT"), "Command output"));
        nameToOutputIndex.put("RESULT", 0);
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        String command = (String) inputs.get(0).getData();
        String commandArguments = (String) inputs.get(1).getData();
        String res="";

        if (!checkGotInputs(1)) {
            runTime = System.currentTimeMillis() - startTime;
            return;
        }

        ProcessBuilder process;

        if(commandArguments!=null)
            process= new ProcessBuilder("cmd.exe","/c",command,commandArguments);

        else
            process= new ProcessBuilder("cmd.exe","/c",command);

        try {
            //add log here
            Process p=process.start();
            res=getCommandOutput(p);

        } catch (IOException e) {
            //add log here
            throw new RuntimeException(e);
        }

        //add log here
        //add summery line here
        outputs.get(0).setData(res);
        runTime = System.currentTimeMillis() - startTime;
    }

    private  String getCommandOutput(Process p)  {
        String res="",line;
        BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(p.getInputStream()));

        try {
            line = reader.readLine();

            while (line != null) {
                line = reader.readLine();
                res += line + "\n";
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return  res;
    }
}
