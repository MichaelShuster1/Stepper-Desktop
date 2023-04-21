package exception;

public class MappingDifferentTypesException extends RuntimeException {
    public MappingDifferentTypesException(String errorMessage) {
        super(errorMessage);
    }
}
