package io.flowinquiry.modules.usermanagement.web.rest.errors;

import io.flowinquiry.exceptions.UserException;

public class InvalidPasswordException extends UserException {

    public InvalidPasswordException() {
        super("Incorrect password");
    }
}
