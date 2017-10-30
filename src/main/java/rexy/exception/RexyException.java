package rexy.exception;

/**
 *
 */
public class RexyException extends Exception {

    public RexyException(String message) {
        super(message);
    }

    public RexyException(String message, Throwable cause) {
        super(message, cause);
    }
}