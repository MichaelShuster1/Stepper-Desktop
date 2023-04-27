package dto;

public class DataDefintionDTO
{
    String name;
    String type;

    public DataDefintionDTO(String name,String type)
    {
        this.name=name;
        this.type=type;
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
