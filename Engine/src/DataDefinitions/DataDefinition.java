package DataDefinitions;

public abstract class DataDefinition
{
    protected String name;
    protected String type;
    protected String typeStream;
    protected boolean mandatory;
    protected boolean userFriendly;

    public DataDefinition(String name, String type, String typeStream, boolean mandatory, boolean userFriendly) {
        this.name = name;
        this.type = type;
        this.typeStream = typeStream;
        this.mandatory = mandatory;
        this.userFriendly = userFriendly;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeStream() {
        return typeStream;
    }

    public void setTypeStream(String typeStream) {
        this.typeStream = typeStream;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public boolean isUserFriendly() {
        return userFriendly;
    }

    public void setUserFriendly(boolean userFriendly) {
        this.userFriendly = userFriendly;
    }
}
