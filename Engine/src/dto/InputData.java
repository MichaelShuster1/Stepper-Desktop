package dto;

public class InputData {
    private final String systemName;
    private final String userString;
    private final Boolean necessity;
    private final Boolean inserted;

    public InputData(String systemName, String userString, Boolean necessity,Boolean inserted) {
        this.systemName = systemName;
        this.userString = userString;
        this.necessity = necessity;
        this.inserted=inserted;
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

    public Boolean IsInserted() {
        return inserted;
    }
}
