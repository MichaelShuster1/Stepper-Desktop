package dto;

public class ResultDTO
{
    private boolean status;
    private String message;

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
