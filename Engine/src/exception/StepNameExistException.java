package exception;

public class StepNameExistException extends RuntimeException
{
    public StepNameExistException(String errorMessage)
    {
        super(errorMessage);
    }
}
