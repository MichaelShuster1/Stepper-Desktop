package exceptions;

public class StepsMappingOrderException extends RuntimeException{
    public StepsMappingOrderException(String errorMessage)
    {
        super(errorMessage);
    }
}
