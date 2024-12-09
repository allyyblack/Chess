package dataaccess;

/**
 * Indicates there was an error with user input
 */

public class UnauthorizedAccessException extends Exception {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
