package exception;

public class InputOutputNotExistException extends RuntimeException {
    public InputOutputNotExistException(String errorMessage) {
        super(errorMessage);
    }
}
