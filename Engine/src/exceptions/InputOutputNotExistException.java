package exceptions;

public class InputOutputNotExistException extends RuntimeException {
    public InputOutputNotExistException(String errorMessage)
    {
        super(errorMessage);
    }
}
