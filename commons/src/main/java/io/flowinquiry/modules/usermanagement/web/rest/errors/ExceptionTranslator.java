package io.flowinquiry.modules.usermanagement.web.rest.errors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionTranslator extends ResponseEntityExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionTranslator.class);

    @ExceptionHandler
    public ResponseEntity<Object> handleAnyException(Throwable ex, NativeWebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");
        HttpStatus status = ExceptionStatusMapper.getHttpStatus(ex);

        if (isUserError(status)) {
            return handleUserError(ex, status, path);
        } else {
            return handleServerError(ex, path);
        }
    }

    private boolean isUserError(HttpStatus status) {
        return status.is4xxClientError();
    }

    private ResponseEntity<Object> handleUserError(Throwable ex, HttpStatus status, String path) {
        LOG.warn("User error ({}): {}", status, ex.getMessage(), ex);

        ErrorResponse errorResponse =
                new ErrorResponse(status.value(), status.getReasonPhrase(), ex.getMessage(), path);
        return ResponseEntity.status(status).body(errorResponse);
    }

    private ResponseEntity<Object> handleServerError(Throwable ex, String path) {
        LOG.error("Server error: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse =
                new ErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                        "An unexpected error occurred. Please contact support.",
                        path);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
