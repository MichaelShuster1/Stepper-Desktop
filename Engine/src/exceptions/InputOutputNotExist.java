package exceptions;

public class InputOutputNotExist extends RuntimeException
{
    public  InputOutputNotExist(String errorMessage)
    {
        super(errorMessage);
    }
}
