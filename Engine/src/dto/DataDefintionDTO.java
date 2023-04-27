package dto;

public class DataDefintionDTO
{
    private final String name;
    private final String type;

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
