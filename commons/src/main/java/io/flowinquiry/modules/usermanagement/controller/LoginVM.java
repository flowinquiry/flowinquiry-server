package io.flowinquiry.modules.usermanagement.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** View Model object for storing a user's credentials. */
public class LoginVM {

    @NotNull @Size(min = 1, max = 50)
    private String email;

    @NotNull @Size(min = 4, max = 100)
    private String password;

    private boolean rememberMe;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LoginVM{" + "username='" + email + '\'' + ", rememberMe=" + rememberMe + '}';
    }
}
