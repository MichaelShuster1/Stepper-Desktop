package exceptions;

public class StepNameNotExistException extends RuntimeException {
    public StepNameNotExistException(String errorMessage)
    {
        super(errorMessage);
    }
}
