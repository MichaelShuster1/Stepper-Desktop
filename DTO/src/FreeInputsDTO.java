import java.util.List;

public class FreeInputsDTO
{
    private List<FreeInputData> freeInputs;

    public FreeInputsDTO(List<FreeInputData> freeInputs)
    {
        this.freeInputs = freeInputs;
    }

    public List<FreeInputData> getFreeInputs()
    {
        return freeInputs;
    }
}
