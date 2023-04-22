package exception;

public class OutputNameExistException extends RuntimeException{
    public OutputNameExistException(String errorMessage){
        super(errorMessage);
    }
}
