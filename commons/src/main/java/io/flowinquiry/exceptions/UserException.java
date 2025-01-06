package io.flowinquiry.exceptions;

/**
 * The root exception caused by user behavior. The user exception is caught by global error handler
 * and return the 4xx error to the consumer
 */
public abstract class UserException extends RuntimeException {

    public UserException(String message) {
        super(message);
    }

    public UserException(String message, Throwable cause) {
        super(message, cause);
    }
}
