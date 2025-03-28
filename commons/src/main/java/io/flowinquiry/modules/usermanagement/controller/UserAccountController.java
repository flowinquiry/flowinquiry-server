package io.flowinquiry.modules.usermanagement.controller;

import io.flowinquiry.modules.collab.service.MailService;
import io.flowinquiry.modules.usermanagement.EmailAlreadyUsedException;
import io.flowinquiry.modules.usermanagement.InvalidPasswordException;
import io.flowinquiry.modules.usermanagement.domain.User;
import io.flowinquiry.modules.usermanagement.repository.UserRepository;
import io.flowinquiry.modules.usermanagement.service.UserService;
import io.flowinquiry.modules.usermanagement.service.dto.PasswordChangeDTO;
import io.flowinquiry.modules.usermanagement.service.dto.UserDTO;
import io.flowinquiry.modules.usermanagement.service.dto.UserKey;
import io.flowinquiry.modules.usermanagement.service.mapper.UserMapper;
import io.flowinquiry.security.SecurityUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserAccountController {

    private static class AccountResourceException extends RuntimeException {

        private AccountResourceException(String message) {
            super(message);
        }
    }

    private static final Logger log = LoggerFactory.getLogger(UserAccountController.class);

    private final UserRepository userRepository;

    private final UserService userService;

    private final UserMapper userMapper;

    private final MailService mailService;

    public UserAccountController(
            UserRepository userRepository,
            UserService userService,
            UserMapper userMapper,
            MailService mailService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.userMapper = userMapper;
        this.mailService = mailService;
    }

    /**
     * {@code POST /register} : register the user.
     *
     * @param managedUserVM the managed user View Model.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerAccount(@Valid @RequestBody ManagedUserVM managedUserVM) {
        if (isPasswordLengthInvalid(managedUserVM.getPassword())) {
            throw new InvalidPasswordException();
        }
        UserDTO user =
                userMapper.toDto(
                        userService.registerUser(managedUserVM, managedUserVM.getPassword()));
        mailService.sendActivationEmail(user);
    }

    @GetMapping("/{email}/resend-activation-email")
    public void resendActivationEmail(@Valid @PathVariable("email") @Email String email) {
        Optional<User> user = userRepository.findUserByEmailEqualsIgnoreCase(email);
        if (user.isEmpty()) {
            log.warn("User with email {} not found", email);
            return;
        }
        UserDTO userDTO = userMapper.toDto(user.get());
        mailService.sendCreationEmail(userDTO);
    }

    /**
     * {@code GET /activate} : activate the registered user.
     *
     * @param key the activation key.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be
     *     activated.
     */
    @GetMapping("/activate")
    public void activateAccount(@RequestParam("key") String key) {
        Optional<User> user = userService.activateRegistration(key);
        if (user.isEmpty()) {
            throw new AccountResourceException("No user was found for this activation key");
        }
    }

    /**
     * {@code GET /account} : get the current user.
     *
     * @return the current user.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be
     *     returned.
     */
    @GetMapping("/account")
    public UserDTO getAccount() {
        return userService
                .getUserWithAuthorities()
                .orElseThrow(() -> new AccountResourceException("User could not be found"));
    }

    /**
     * {@code POST /account} : update the current user information.
     *
     * @param userDTO the current user information.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user login wasn't found.
     */
    @PostMapping("/account")
    public void saveAccount(@Valid @RequestBody UserDTO userDTO) {
        String userLogin =
                SecurityUtils.getCurrentUserLogin()
                        .map(UserKey::getEmail)
                        .orElseThrow(
                                () -> new AccountResourceException("Current user login not found"));
        Optional<User> user = userRepository.findOneByEmailIgnoreCase(userLogin);
        if (user.isEmpty()) {
            throw new AccountResourceException("User could not be found");
        }
        if (!user.get().getEmail().equals(userDTO.getEmail())) {
            throw new AccountResourceException("Can not change email");
        }
        userService.updateUser(
                userDTO.getFirstName(),
                userDTO.getLastName(),
                userDTO.getEmail(),
                userDTO.getLangKey(),
                userDTO.getImageUrl());
    }

    /**
     * {@code POST /account/change-password} : changes the current user's password.
     *
     * @param passwordChangeDto current and new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the new password is incorrect.
     */
    @PostMapping(path = "/account/change-password")
    public void changePassword(@RequestBody PasswordChangeDTO passwordChangeDto) {
        if (isPasswordLengthInvalid(passwordChangeDto.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        userService.changePassword(
                passwordChangeDto.getCurrentPassword(), passwordChangeDto.getNewPassword());
    }

    /**
     * {@code POST /account/reset-password/init} : Send an email to reset the password of the user.
     *
     * @param email the mail of the user.
     */
    @GetMapping(path = "/account/reset-password/init")
    public void requestPasswordReset(@RequestParam("email") @Email String email) {
        Optional<UserDTO> user = userService.requestPasswordReset(email);
        if (user.isPresent()) {
            mailService.sendPasswordResetMail(user.orElseThrow());
        } else {
            // Pretend the request has been successful to prevent checking which emails really exist
            // but log that an invalid attempt has been made
            log.warn("Password reset requested for non existing mail {}", email);
        }
    }

    /**
     * {@code POST /account/reset-password/finish} : Finish to reset the password of the user.
     *
     * @param keyAndPassword the generated key and the new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the password could not be
     *     reset.
     */
    @PostMapping(path = "/account/reset-password/finish")
    public void finishPasswordReset(@RequestBody KeyAndPasswordVM keyAndPassword) {
        if (isPasswordLengthInvalid(keyAndPassword.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        Optional<User> user =
                userService.completePasswordReset(
                        keyAndPassword.getNewPassword(), keyAndPassword.getKey());

        if (user.isEmpty()) {
            throw new AccountResourceException("No user was found for this reset key");
        }
    }

    private static boolean isPasswordLengthInvalid(String password) {
        return (StringUtils.isEmpty(password)
                || password.length() < ManagedUserVM.PASSWORD_MIN_LENGTH
                || password.length() > ManagedUserVM.PASSWORD_MAX_LENGTH);
    }
}
