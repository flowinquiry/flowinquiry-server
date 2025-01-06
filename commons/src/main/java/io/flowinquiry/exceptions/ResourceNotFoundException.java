package io.flowinquiry.exceptions;

/** Exception thrown if there is no resource found */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
