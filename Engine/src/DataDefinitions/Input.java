package DataDefinitions;

import java.io.Serializable;

public class Input implements Serializable
{
    private DataDefinition dataDefinition;
    private final boolean user_friendly;
    private final boolean mandatory;
    private final String userString;
    private boolean isConnected;

    public Input(DataDefinition dataDefinition, boolean user_friendly, boolean mandatory, String userString)
    {
        this.dataDefinition = dataDefinition;
        this.user_friendly = user_friendly;
        this.mandatory = mandatory;
        this.isConnected = false;
        this.userString = userString;
    }

    public Object getData()
    {
        return dataDefinition.getData();
    }

    public void setData(Object data)
    {
        dataDefinition.setData(data);
    }

    public void setName(String name)
    {
        dataDefinition.setName(name);
    }

    public String getName()
    {
        return  dataDefinition.getName();
    }

    public String getType()
    {
        return  dataDefinition.getType();
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public boolean isMandatory() {return mandatory;}

    public boolean isUser_friendly() {
        return user_friendly;
    }

    public String getUserString() {
        return userString;
    }

    public void resetInput()
    {
        setData(null);
        isConnected = false;
    }
}
