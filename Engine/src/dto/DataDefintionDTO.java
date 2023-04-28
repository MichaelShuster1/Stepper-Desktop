package dto;

import java.io.Serializable;

public class DataDefintionDTO implements Serializable
{
    private final String name;
    private final String type;

    public DataDefintionDTO(String name,String type)
    {
        this.name=name;
        this.type=type.substring(4);
    }

    public DataDefintionDTO(DataDefintionDTO other)
    {
        this.name=other.name;
        this.type=other.type;
    }


    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
