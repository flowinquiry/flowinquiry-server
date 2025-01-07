package io.flowinquiry.modules.usermanagement;

import io.flowinquiry.exceptions.UserException;

public class InvalidPasswordException extends UserException {

    public InvalidPasswordException() {
        this("Incorrect password");
    }

    public InvalidPasswordException(String message) {
        super(message);
    }
}
