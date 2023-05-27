package dto;

public class OutputExecutionDTO extends DataDefintionDTO {
    private Object data;

    public OutputExecutionDTO(DataDefintionDTO other, Object data) {
        super(other);
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
