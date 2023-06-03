package initialvalue;

public class InitialValue {
    String inputName;
    String Data;
    int stepIndex;

    String type;

    public InitialValue(String inputName, String data, int stepIndex, String type) {
        this.inputName = inputName;
        Data = data;
        this.stepIndex = stepIndex;
        this.type = type;
    }

    public String getInputName() {
        return inputName;
    }

    public String getData() {
        return Data;
    }

    public int getStepIndex() {
        return stepIndex;
    }

    public String getType() {
        return type;
    }
}
