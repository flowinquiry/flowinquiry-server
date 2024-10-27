package io.flexwork.modules.usermanagement.service;

import static io.flexwork.query.QueryUtils.createSpecification;

import io.flexwork.modules.usermanagement.AuthoritiesConstants;
import io.flexwork.modules.usermanagement.domain.Authority;
import io.flexwork.modules.usermanagement.domain.User;
import io.flexwork.modules.usermanagement.domain.User_;
import io.flexwork.modules.usermanagement.repository.AuthorityRepository;
import io.flexwork.modules.usermanagement.repository.UserRepository;
import io.flexwork.modules.usermanagement.service.dto.UserDTO;
import io.flexwork.modules.usermanagement.service.dto.UserKey;
import io.flexwork.modules.usermanagement.service.mapper.UserMapper;
import io.flexwork.query.QueryDTO;
import io.flexwork.security.Constants;
import io.flexwork.security.SecurityUtils;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.security.RandomUtil;

/** Service class for managing users. */
@Service
@Transactional
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthorityRepository authorityRepository;

    private final UserMapper userMapper;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthorityRepository authorityRepository,
            UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
        this.userMapper = userMapper;
    }

    public Optional<User> activateRegistration(String key) {
        LOG.debug("Activating user for activation key {}", key);
        return userRepository
                .findOneByActivationKey(key)
                .map(
                        user -> {
                            // activate given user for the registration key.
                            user.setActivated(true);
                            user.setActivationKey(null);
                            LOG.debug("Activated user: {}", user);
                            return user;
                        });
    }

    public Optional<User> completePasswordReset(String newPassword, String key) {
        LOG.debug("Reset user password for reset key {}", key);
        return userRepository
                .findOneByResetKey(key)
                .filter(
                        user ->
                                user.getResetDate()
                                        .isAfter(Instant.now().minus(1, ChronoUnit.DAYS)))
                .map(
                        user -> {
                            user.setPassword(passwordEncoder.encode(newPassword));
                            user.setResetKey(null);
                            user.setResetDate(null);
                            return user;
                        });
    }

    public Optional<User> requestPasswordReset(String mail) {
        return userRepository
                .findOneByEmailIgnoreCase(mail)
                .filter(User::isActivated)
                .map(
                        user -> {
                            user.setResetKey(RandomUtil.generateResetKey());
                            user.setResetDate(Instant.now());
                            return user;
                        });
    }

    public User registerUser(UserDTO userDTO, String password) {
        userRepository
                .findOneByEmailIgnoreCase(userDTO.getEmail().toLowerCase())
                .ifPresent(
                        existingUser -> {
                            boolean removed = removeNonActivatedUser(existingUser);
                            if (!removed) {
                                throw new UsernameAlreadyUsedException();
                            }
                        });
        userRepository
                .findOneByEmailIgnoreCase(userDTO.getEmail())
                .ifPresent(
                        existingUser -> {
                            boolean removed = removeNonActivatedUser(existingUser);
                            if (!removed) {
                                throw new EmailAlreadyUsedException();
                            }
                        });
        User newUser = new User();
        String encryptedPassword = passwordEncoder.encode(password);
        // new user gets initially a generated password
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(userDTO.getFirstName());
        newUser.setLastName(userDTO.getLastName());
        if (userDTO.getEmail() != null) {
            newUser.setEmail(userDTO.getEmail().toLowerCase());
        }
        newUser.setImageUrl(userDTO.getImageUrl());
        newUser.setLangKey(userDTO.getLangKey());
        // new user is not active
        newUser.setActivated(false);
        // new user gets registration key
        newUser.setActivationKey(RandomUtil.generateActivationKey());
        Set<Authority> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        newUser.setAuthorities(authorities);
        userRepository.save(newUser);
        LOG.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    private boolean removeNonActivatedUser(User existingUser) {
        if (existingUser.isActivated()) {
            return false;
        }
        userRepository.delete(existingUser);
        userRepository.flush();
        return true;
    }

    public User createUser(UserDTO userDTO) {
        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail().toLowerCase());
        }
        user.setImageUrl(userDTO.getImageUrl());
        if (userDTO.getLangKey() == null) {
            user.setLangKey(Constants.DEFAULT_LANGUAGE); // default language
        } else {
            user.setLangKey(userDTO.getLangKey());
        }
        String encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());
        user.setPassword(encryptedPassword);
        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(Instant.now());
        user.setActivated(true);

        // Due to client when construct the authority, it passes the Authority object with the name
        // and descriptiveName have both actual value
        // is descriptiveName, so we must mapping the authority by search authority by descriptive
        // name
        if (userDTO.getAuthorities() != null) {
            Set<Authority> authorities =
                    userDTO.getAuthorities().stream()
                            .map(
                                    authorityDTO ->
                                            authorityRepository.findByDescriptiveName(
                                                    authorityDTO.getDescriptiveName()))
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .collect(Collectors.toSet());
            user.setAuthorities(authorities);
        }
        userRepository.save(user);
        LOG.debug("Created Information for User: {}", user);
        return user;
    }

    /**
     * Update all information for a specific user, and return the modified user.
     *
     * @param userDTO user to update.
     * @return updated user.
     */
    public Optional<User> updateUser(UserDTO userDTO) {
        return Optional.of(userRepository.findById(userDTO.getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(
                        user -> {
                            user.setFirstName(userDTO.getFirstName());
                            user.setLastName(userDTO.getLastName());
                            if (userDTO.getEmail() != null) {
                                user.setEmail(userDTO.getEmail().toLowerCase());
                            }
                            user.setImageUrl(userDTO.getImageUrl());
                            user.setActivated(userDTO.isActivated());
                            user.setLangKey(userDTO.getLangKey());
                            Set<Authority> managedAuthorities = user.getAuthorities();
                            managedAuthorities.clear();
                            userDTO.getAuthorities().stream()
                                    .map(
                                            authorityDTO ->
                                                    authorityRepository.findById(
                                                            authorityDTO.getName()))
                                    .filter(Optional::isPresent)
                                    .map(Optional::get)
                                    .forEach(managedAuthorities::add);
                            userRepository.save(user);
                            LOG.debug("Changed Information for User: {}", user);
                            return user;
                        });
    }

    public void deleteUserByEmail(String email) {
        userRepository
                .findOneByEmailIgnoreCase(email)
                .ifPresent(
                        user -> {
                            userRepository.delete(user);
                            LOG.debug("Deleted User: {}", user);
                        });
    }

    /**
     * Update basic information (first name, last name, email, language) for the current user.
     *
     * @param firstName first name of user.
     * @param lastName last name of user.
     * @param email email id of user.
     * @param langKey language key.
     * @param imageUrl image URL of user.
     */
    public void updateUser(
            String firstName, String lastName, String email, String langKey, String imageUrl) {
        SecurityUtils.getCurrentUserLogin()
                .map(UserKey::getEmail)
                .flatMap(userRepository::findOneByEmailIgnoreCase)
                .ifPresent(
                        user -> {
                            user.setFirstName(firstName);
                            user.setLastName(lastName);
                            if (email != null) {
                                user.setEmail(email.toLowerCase());
                            }
                            user.setLangKey(langKey);
                            user.setImageUrl(imageUrl);
                            userRepository.save(user);
                            LOG.debug("Changed Information for User: {}", user);
                        });
    }

    @Transactional
    public void changePassword(String currentClearTextPassword, String newPassword) {
        SecurityUtils.getCurrentUserLogin()
                .map(UserKey::getEmail)
                .flatMap(userRepository::findOneByEmailIgnoreCase)
                .ifPresent(
                        user -> {
                            String currentEncryptedPassword = user.getPassword();
                            if (!passwordEncoder.matches(
                                    currentClearTextPassword, currentEncryptedPassword)) {
                                throw new InvalidPasswordException();
                            }
                            String encryptedPassword = passwordEncoder.encode(newPassword);
                            user.setPassword(encryptedPassword);
                            LOG.debug("Changed password for User: {}", user);
                        });
    }

    @Transactional(readOnly = true)
    public Page<User> findAllManagedUsers(Optional<QueryDTO> queryDTO, Pageable pageable) {
        Specification<User> spec = createSpecification(queryDTO);
        return userRepository.findAll(spec, pageable);
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> findAllPublicUsers(Optional<QueryDTO> queryDTO, Pageable pageable) {
        Specification<User> spec = createSpecification(queryDTO);
        if (spec == null) {
            spec = Specification.where(null);
        }
        spec.and(((root, query, criteriaBuilder) -> criteriaBuilder.isNotNull(root.get(User_.ID))))
                .and(
                        ((root, query, criteriaBuilder) ->
                                criteriaBuilder.isTrue(root.get(User_.ACTIVATED))));
        return userRepository.findAll(spec, pageable).map(userMapper::userToUserDTO);
    }

    public Page<UserDTO> getUsersByTeam(Long teamId, Pageable pageable) {
        return userRepository.findAllByTeamId(teamId, pageable).map(userMapper::userToUserDTO);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthoritiesByEmail(String email) {
        return userRepository.findOneWithAuthoritiesByEmailIgnoreCase(email);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities() {
        return SecurityUtils.getCurrentUserLogin()
                .map(UserKey::getEmail)
                .flatMap(userRepository::findOneWithAuthoritiesByEmailIgnoreCase);
    }

    //
    //    /**
    //     * Not activated users should be automatically deleted after 3 days.
    //     *
    //     * <p>This is scheduled to get fired everyday, at 01:00 (am).
    //     */
    //    @Scheduled(cron = "0 0 1 * * ?")
    //    public void removeNotActivatedUsers() {
    //        userRepository
    //                .findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(
    //                        Instant.now().minus(3, ChronoUnit.DAYS))
    //                .forEach(
    //                        user -> {
    //                            log.debug("Deleting not activated user {}", user.getLogin());
    //                            userRepository.delete(user);
    //                        });
    //    }

    /**
     * Gets a list of all the authorities.
     *
     * @return a list of all the authorities.
     */
    @Transactional(readOnly = true)
    public List<String> getAuthorities() {
        return authorityRepository.findAll().stream().map(Authority::getName).toList();
    }
}
