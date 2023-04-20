package exceptions;

public class SameOutputNameException extends RuntimeException {
    public SameOutputNameException(String errorMessage)
    {
        super(errorMessage);
    }
}

