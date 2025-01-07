package io.flowinquiry.modules.usermanagement.service;

import io.flowinquiry.modules.usermanagement.UserNotActivatedException;
import io.flowinquiry.modules.usermanagement.domain.User;
import io.flowinquiry.modules.usermanagement.domain.UserStatus;
import io.flowinquiry.modules.usermanagement.repository.UserRepository;
import io.flowinquiry.modules.usermanagement.service.dto.FwUserDetails;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Authenticate a user from the database. */
@Component("userDetailsService")
public class DomainUserDetailsService implements UserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(DomainUserDetailsService.class);

    private final UserRepository userRepository;

    public DomainUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(final String email) {
        LOG.debug("Authenticating {}", email);

        if (new EmailValidator().isValid(email, null)) {
            return userRepository
                    .findOneWithAuthoritiesAndUserAuthsByEmailIgnoreCase(email)
                    .map(user -> createSpringSecurityUser(email, user))
                    .orElseThrow(
                            () ->
                                    new UsernameNotFoundException(
                                            "User with email "
                                                    + email
                                                    + " was not found in the database"));
        } else {
            throw new IllegalArgumentException("Invalid email: " + email);
        }
    }

    private FwUserDetails createSpringSecurityUser(String lowercaseLogin, User user) {
        if (!user.getStatus().equals(UserStatus.ACTIVE)) {
            throw new UserNotActivatedException("User " + lowercaseLogin + " was not activated");
        }
        return new FwUserDetails(user);
    }
}
