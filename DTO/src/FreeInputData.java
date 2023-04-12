public class FreeInputData
{
    private String systemName;
    private String userString;
    private Boolean necessity;

    public FreeInputData(String systemName, String userString, Boolean necessity)
    {
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
