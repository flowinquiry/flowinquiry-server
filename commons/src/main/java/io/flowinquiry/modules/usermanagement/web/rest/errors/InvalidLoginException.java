package io.flowinquiry.modules.usermanagement.web.rest.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;
import tech.jhipster.web.rest.errors.ProblemDetailWithCause;

public class InvalidLoginException extends ErrorResponseException {
    public InvalidLoginException() {
        super(
                HttpStatus.BAD_REQUEST,
                ProblemDetailWithCause.ProblemDetailWithCauseBuilder.instance()
                        .withStatus(HttpStatus.BAD_REQUEST.value())
                        .withTitle("Invalid login")
                        .build(),
                null);
    }
}
