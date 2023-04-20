package exceptions;

public class FlowNameExistException extends RuntimeException
{
    public FlowNameExistException(String errorMessage)
    {
        super(errorMessage);
    }
}
