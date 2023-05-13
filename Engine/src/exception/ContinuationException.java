package exception;

public class ContinuationException extends RuntimeException{
    public ContinuationException(String errorMessage) {
        super(errorMessage);
    }
}
