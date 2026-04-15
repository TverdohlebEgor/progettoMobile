package cohappy.backend.exceptions;

public class FundInsufficientException extends RuntimeException {
    public FundInsufficientException(String message) {
        super(message);
    }
}
