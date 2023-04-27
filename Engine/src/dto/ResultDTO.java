package dto;

public class ResultDTO {
    private final boolean status;
    private final String message;

    public ResultDTO(boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    public boolean getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
