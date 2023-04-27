package dto;

public class FreeInputExecutionDTO extends DataDefintionDTO{
    private final String data;
    private final boolean necessity;

    public FreeInputExecutionDTO(DataDefintionDTO other, String data, boolean necessity) {
        super(other);
        this.data = data;
        this.necessity = necessity;
    }

    public String getData() {
        return data;
    }

    public boolean isMandatory() {
        return necessity;
    }
}
