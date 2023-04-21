package exception;

public class InputNameExistException extends RuntimeException{
    public InputNameExistException(String errorMessage)
    {
        super(errorMessage);
    }
}
