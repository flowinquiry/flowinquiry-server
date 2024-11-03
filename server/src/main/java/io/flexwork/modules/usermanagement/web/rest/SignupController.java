package io.flexwork.modules.usermanagement.web.rest;

import io.flexwork.modules.usermanagement.domain.User;
import io.flexwork.modules.usermanagement.service.SignupService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class SignupController {

    private static final Logger LOG = LoggerFactory.getLogger(SignupController.class);

    private SignupService signupService;

    public SignupController(SignupService signupService) {
        this.signupService = signupService;
    }

    @PostMapping(value = "/signup")
    public void signup(@RequestBody User user) {
        signupService.signup(user);
    }

    @PostMapping("/forgot-password")
    public void forgotPassword(@Valid @RequestBody PasswordResetRequest request) {
        LOG.debug("Forgot password for user {}", request);
    }

    @Data
    public static class PasswordResetRequest {

        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        private String email;
    }
}
