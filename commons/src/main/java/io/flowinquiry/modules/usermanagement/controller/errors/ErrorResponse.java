package io.flowinquiry.modules.usermanagement.controller.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String error;
    private String message;
    private String path;
}
