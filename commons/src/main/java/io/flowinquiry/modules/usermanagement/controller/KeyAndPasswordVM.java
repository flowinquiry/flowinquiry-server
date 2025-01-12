package io.flowinquiry.modules.usermanagement.controller;

import lombok.Data;

/** View Model object for storing the user's key and password. */
@Data
public class KeyAndPasswordVM {

    private String key;

    private String newPassword;
}
