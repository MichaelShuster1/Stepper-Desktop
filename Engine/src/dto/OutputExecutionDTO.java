package dto;

public class OutputExecutionDTO extends DataDefintionDTO{
    private String data;

    public OutputExecutionDTO(DataDefintionDTO other, String data) {
        super(other);
        this.data = data;
    }

    public String getData() {
        return data;
    }
}
