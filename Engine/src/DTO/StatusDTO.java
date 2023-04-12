package DTO;

public class StatusDTO
{
    private boolean status;
    private String message;

    public StatusDTO(boolean status, String message) {
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
