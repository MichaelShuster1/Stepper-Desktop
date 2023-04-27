package dto;

public class InputData {
    private final String systemName;
    private final String userString;
    private final Boolean necessity;

    public InputData(String systemName, String userString, Boolean necessity) {
        this.systemName = systemName;
        this.userString = userString;
        this.necessity = necessity;
    }

    public String getSystemName() {
        return systemName;
    }

    public String getUserString() {
        return userString;
    }

    public Boolean getNecessity() {
        return necessity;
    }
}
