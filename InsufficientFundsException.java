// Custom (checked) exception thrown when a withdrawal is larger than the funds available
public class InsufficientFundsException extends Exception {

    public InsufficientFundsException(String message) {
        super(message);
    }
}
