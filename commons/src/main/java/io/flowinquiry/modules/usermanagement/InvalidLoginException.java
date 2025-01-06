package io.flowinquiry.modules.usermanagement;

import io.flowinquiry.exceptions.UserException;

public class InvalidLoginException extends UserException {
    public InvalidLoginException() {
        super("Invalid login");
    }
}
