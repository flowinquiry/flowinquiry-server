package io.flowinquiry.modules.usermanagement.web.rest.errors;

import io.flowinquiry.exceptions.UserException;

public class InvalidLoginException extends UserException {
    public InvalidLoginException() {
        super("Invalid login");
    }
}
