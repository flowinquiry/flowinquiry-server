package io.flexwork.modules.usermanagement.web.rest;

import io.flexwork.modules.collab.service.MailService;
import io.flexwork.modules.usermanagement.AuthoritiesConstants;
import io.flexwork.modules.usermanagement.domain.User;
import io.flexwork.modules.usermanagement.repository.UserRepository;
import io.flexwork.modules.usermanagement.service.UserService;
import io.flexwork.modules.usermanagement.service.dto.UserDTO;
import io.flexwork.modules.usermanagement.service.mapper.UserMapper;
import io.flexwork.modules.usermanagement.web.rest.errors.BadRequestAlertException;
import io.flexwork.modules.usermanagement.web.rest.errors.EmailAlreadyUsedException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import java.net.URI;
import java.net.URISyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;

/**
 * REST controller for managing users.
 *
 * <p>This class accesses the {@link User} entity, and needs to fetch its collection of authorities.
 *
 * <p>For a normal use-case, it would be better to have an eager relationship between User and
 * Authority, and send everything to the client side: there would be no View Model and DTO, a lot
 * less code, and an outer-join which would be good for performance.
 *
 * <p>We use a View Model and a DTO for 3 reasons:
 *
 * <ul>
 *   <li>We want to keep a lazy association between the user and the authorities, because people
 *       will quite often do relationships with the user, and we don't want them to get the
 *       authorities all the time for nothing (for performance reasons). This is the #1 goal: we
 *       should not impact our users' application because of this use-case.
 *   <li>Not having an outer join causes n+1 requests to the database. This is not a real issue as
 *       we have by default a second-level cache. This means on the first HTTP call we do the n+1
 *       requests, but then all authorities come from the cache, so in fact it's much better than
 *       doing an outer join (which will get lots of data from the database, for each HTTP call).
 *   <li>As this manages users, for security reasons, we'd rather have a DTO layer.
 * </ul>
 *
 * <p>Another option would be to have a specific JPA entity graph to handle this case.
 */
@RestController
@RequestMapping("/api/admin")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @Value("${spring.application.name}")
    private String applicationName;

    private final UserService userService;

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final MailService mailService;

    public UserController(
            UserService userService,
            UserRepository userRepository,
            UserMapper userMapper,
            MailService mailService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.mailService = mailService;
    }

    /**
     * {@code POST /admin/users} : Creates a new user.
     *
     * <p>Creates a new user if the login and email are not already used, and sends an mail with an
     * activation link. The user needs to be activated on creation.
     *
     * @param userDTO the user to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new
     *     user, or with status {@code 400 (Bad Request)} if the login or email is already in use.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     * @throws BadRequestAlertException {@code 400 (Bad Request)} if the login or email is already
     *     in use.
     */
    @PostMapping("/users")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO)
            throws URISyntaxException {
        LOG.debug("REST request to save User : {}", userDTO);

        if (userDTO.getId() != null) {
            throw new BadRequestAlertException(
                    "A new user cannot already have an ID", "userManagement", "idexists");
            // Lowercase the user login before comparing with database
        } else if (userRepository.findOneByEmailIgnoreCase(userDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyUsedException();
        } else {
            UserDTO newUser = userMapper.toDto(userService.createUser(userDTO));
            mailService.sendCreationEmail(newUser);
            return ResponseEntity.created(new URI("/api/admin/users/" + newUser.getEmail()))
                    .headers(
                            HeaderUtil.createAlert(
                                    applicationName, "userManagement.created", newUser.getEmail()))
                    .body(newUser);
        }
    }

    /**
     * {@code DELETE /admin/users/:login} : delete the "login" User.
     *
     * @param login the login of the user to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/users/{login}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Void> deleteUser(@PathVariable("login") @Email String login) {
        LOG.debug("REST request to delete User: {}", login);
        userService.deleteUserByEmail(login);
        return ResponseEntity.noContent()
                .headers(HeaderUtil.createAlert(applicationName, "userManagement.deleted", login))
                .build();
    }
}
