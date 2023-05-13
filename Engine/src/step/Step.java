package step;

import datadefinition.Input;
import datadefinition.Output;
import dto.StepExecutionDTO;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

public abstract class Step implements Serializable {

    protected String name;

    protected String defaultName;
    protected final boolean read_only;
    protected final boolean continueIfFailing;
    protected State stateAfterRun;

    protected List<Input> inputs;
    protected Map<String, Integer> nameToInputIndex;

    protected List<Output> outputs;
    protected Map<String, Integer> nameToOutputIndex;
    protected List<String> log;

    protected String summaryLine;

    protected Long runTime;

    protected Step(String name, boolean read_only, boolean continueIfFailing) {
        this.name = name;
        this.read_only = read_only;
        this.continueIfFailing = continueIfFailing;
        inputs = new ArrayList<>();
        outputs = new ArrayList<>();
        log = new ArrayList<>();
        nameToInputIndex = new HashMap<>();
        nameToOutputIndex = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public String getDefaultName() {
        return defaultName;
    }

    public boolean isRead_only() {
        return read_only;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void changeInputName(String oldName, String newName) {
        Integer index = nameToInputIndex.remove(oldName);
        nameToInputIndex.put(newName, index);
        inputs.get(index).setName(newName);
    }

    public void changeOutputName(String oldName, String newName) {
        Integer index = nameToOutputIndex.remove(oldName);
        nameToOutputIndex.put(newName, index);
        outputs.get(index).setName(newName);
    }

    public StepExecutionDTO getStepExecutionData() {
        return new StepExecutionDTO(name,runTime, stateAfterRun.toString(),summaryLine, log);
    }

    public State getStateAfterRun() {
        return stateAfterRun;
    }

    public void setStateAfterRun(State stateAfterRun) {
        this.stateAfterRun = stateAfterRun;
    }

    protected void addLineToLog(String line) {

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss.sss");
        formatter.format(new Date());
        log.add(line + "\n[time: " + formatter.format(new Date()) + "]");
    }

    public Input getInput(int index) {
        return inputs.get(index);
    }

    public Output getOutput(int index) {
        return outputs.get(index);
    }

    public List<Input> getInputs() {
        return inputs;
    }

    public List<Output> getOutputs() {
        return outputs;
    }

    public boolean isContinueIfFailing() {
        return continueIfFailing;
    }

    public List<String> getLog() {
        return log;
    }

    public Map<String, Integer> getNameToInputIndex() {
        return nameToInputIndex;
    }

    public Map<String, Integer> getNameToOutputIndex() {
        return nameToOutputIndex;
    }

    public Input getInputByName(String name) {
        return inputs.get(getNameToInputIndex().get(name));
    }

    public Output getOutputByName(String name) {return outputs.get(getNameToOutputIndex().get(name)); }


    public Long getRunTime() {
        return runTime;
    }

    public void resetStep() {
        log.clear();
        summaryLine = null;
        runTime = null;

        for (Input input : inputs)
            input.resetInput();

        for (Output output : outputs)
            output.resetOutput();
    }

    public abstract void run();

    protected boolean checkGotInputs(int numOfInputs) {
        boolean isValid = true;
        for (int i = 0; i < numOfInputs; i++) {
            if (inputs.get(i).getData() == null)
                isValid = false;
        }

        if (!isValid) {
            addLineToLog("Trying to access inputs, but no inputs received");
            setStateAfterRun(State.FAILURE);
            summaryLine = "Step failed, mandatory inputs not received";
        }

        return isValid;
    }
}

