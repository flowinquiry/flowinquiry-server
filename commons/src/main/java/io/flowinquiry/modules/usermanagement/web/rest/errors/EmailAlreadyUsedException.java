package io.flowinquiry.modules.usermanagement.web.rest.errors;

import io.flowinquiry.exceptions.UserException;

public class EmailAlreadyUsedException extends UserException {

    public EmailAlreadyUsedException() {
        super("Email is already in use!");
    }
}
