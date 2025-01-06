package io.flowinquiry.exceptions;

/** The exception when the system can not save / update/ delete resource because some constraint */
public class ResourceConstraintException extends UserException {

    public ResourceConstraintException(String message) {
        super(message);
    }
}
