package io.flexwork.web.rest;

import static io.flexwork.db.DbConstants.DEFAULT_TENANT;
import static io.flexwork.db.TenantConstants.HEADER_TENANT_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.flexwork.DefaultTenantContext;
import io.flexwork.IntegrationTest;
import io.flexwork.modules.usermanagement.AuthoritiesConstants;
import io.flexwork.modules.usermanagement.domain.User;
import io.flexwork.modules.usermanagement.repository.AuthorityRepository;
import io.flexwork.modules.usermanagement.repository.UserRepository;
import io.flexwork.modules.usermanagement.service.UserService;
import io.flexwork.modules.usermanagement.service.dto.AuthorityDTO;
import io.flexwork.modules.usermanagement.service.dto.PasswordChangeDTO;
import io.flexwork.modules.usermanagement.service.dto.UserDTO;
import io.flexwork.modules.usermanagement.service.mapper.UserMapper;
import io.flexwork.modules.usermanagement.web.rest.KeyAndPasswordVM;
import io.flexwork.modules.usermanagement.web.rest.ManagedUserVM;
import io.flexwork.modules.usermanagement.web.rest.UserAccountController;
import io.flexwork.security.Constants;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/** Integration tests for the {@link UserAccountController} REST controller. */
@AutoConfigureMockMvc
@IntegrationTest
@DefaultTenantContext
class UserAccountControllerIT {

    static final String TEST_USER_LOGIN_EMAIL = "test@localhost.io";

    @Autowired private ObjectMapper om;

    @Autowired private UserRepository userRepository;

    @Autowired private AuthorityRepository authorityRepository;

    @Autowired private UserService userService;

    @Autowired private UserMapper userMapper;

    @Autowired private PasswordEncoder passwordEncoder;

    @Autowired private MockMvc restAccountMockMvc;

    private Long numberOfUsers;

    @BeforeEach
    public void countUsers() {
        numberOfUsers = userRepository.count();
    }

    @AfterEach
    public void cleanupAndCheck() {
        assertThat(userRepository.count()).isEqualTo(numberOfUsers);
        numberOfUsers = null;
    }

    @Test
    @WithUnauthenticatedMockUser
    void testNonAuthenticatedUser() throws Exception {
        restAccountMockMvc
                .perform(
                        get("/api/authenticate")
                                .header(HEADER_TENANT_ID, DEFAULT_TENANT)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    @WithMockUser(TEST_USER_LOGIN_EMAIL)
    void testAuthenticatedUser() throws Exception {
        restAccountMockMvc
                .perform(
                        get("/api/authenticate")
                                .header(HEADER_TENANT_ID, DEFAULT_TENANT)
                                .with(request -> request)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(TEST_USER_LOGIN_EMAIL));
    }

    @Test
    @WithMockUser(TEST_USER_LOGIN_EMAIL)
    void testGetExistingAccount() throws Exception {
        Set<AuthorityDTO> authorities = new HashSet<>();
        authorities.add(new AuthorityDTO(AuthoritiesConstants.ADMIN));

        UserDTO user = new UserDTO();
        user.setFirstName("john");
        user.setLastName("doe");
        user.setEmail(TEST_USER_LOGIN_EMAIL);
        user.setImageUrl("http://placehold.it/50x50");
        user.setLangKey("en");
        user.setAuthorities(authorities);
        userService.createUser(user);

        restAccountMockMvc
                .perform(
                        get("/api/account")
                                .header(HEADER_TENANT_ID, DEFAULT_TENANT)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.firstName").value("john"))
                .andExpect(jsonPath("$.lastName").value("doe"))
                .andExpect(jsonPath("$.email").value(TEST_USER_LOGIN_EMAIL))
                .andExpect(jsonPath("$.imageUrl").value("http://placehold.it/50x50"))
                .andExpect(jsonPath("$.langKey").value("en"))
                .andExpect(
                        jsonPath("$.authorities")
                                .value(
                                        hasItems(
                                                hasEntry("name", "ROLE_ADMIN"),
                                                hasEntry("descriptiveName", "Administrator"))));
        userService.deleteUserByEmail(TEST_USER_LOGIN_EMAIL);
    }

    @Test
    void testGetUnknownAccount() throws Exception {
        restAccountMockMvc
                .perform(
                        get("/api/account")
                                .header(HEADER_TENANT_ID, DEFAULT_TENANT)
                                .accept(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    void testRegisterValid() throws Exception {
        ManagedUserVM validUser = new ManagedUserVM();
        validUser.setPassword("password");
        validUser.setFirstName("Alice");
        validUser.setLastName("Test");
        validUser.setEmail("test-register-valid@example.com");
        validUser.setImageUrl("http://placehold.it/50x50");
        validUser.setLangKey(Constants.DEFAULT_LANGUAGE);
        validUser.setAuthorities(
                Collections.singleton(new AuthorityDTO(AuthoritiesConstants.ADMIN, "Admin")));
        assertThat(userRepository.findOneByEmailIgnoreCase("test-register-valid@example.com"))
                .isEmpty();

        restAccountMockMvc
                .perform(
                        post("/api/register")
                                .header(HEADER_TENANT_ID, DEFAULT_TENANT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsBytes(validUser)))
                .andExpect(status().isCreated());

        assertThat(userRepository.findOneByEmailIgnoreCase("test-register-valid@example.com"))
                .isPresent();

        userService.deleteUserByEmail("test-register-valid@example.com");
    }

    @Test
    @Transactional
    void testRegisterValidLogin() throws Exception {
        ManagedUserVM invalidUser = new ManagedUserVM();
        invalidUser.setPassword("password");
        invalidUser.setFirstName("Funky");
        invalidUser.setLastName("One");
        invalidUser.setEmail("funky@example.com");
        invalidUser.setActivated(true);
        invalidUser.setImageUrl("http://placehold.it/50x50");
        invalidUser.setLangKey(Constants.DEFAULT_LANGUAGE);
        invalidUser.setAuthorities(
                Collections.singleton(new AuthorityDTO(AuthoritiesConstants.USER, "User")));

        restAccountMockMvc
                .perform(
                        post("/api/register")
                                .header(HEADER_TENANT_ID, DEFAULT_TENANT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsBytes(invalidUser)))
                .andExpect(status().isCreated());

        Optional<User> user = userRepository.findOneByEmailIgnoreCase("funky@example.com");
        assertThat(user).isNotEmpty();
        userService.deleteUserByEmail("funky@example.com");
    }

    static Stream<ManagedUserVM> invalidUsers() {
        return Stream.of(
                createInvalidUser("password", "Bob", "Green", "invalid", true), // <-- invalid
                createInvalidUser(
                        "123",
                        "Bob",
                        "Green",
                        "bob@example.com",
                        true), // password with only 3 digits
                createInvalidUser(
                        null, "Bob", "Green", "bob@example.com", true) // invalid null password
                );
    }

    @ParameterizedTest
    @MethodSource("invalidUsers")
    @Transactional
    void testRegisterInvalidUsers(ManagedUserVM invalidUser) throws Exception {
        restAccountMockMvc
                .perform(
                        post("/api/register")
                                .header(HEADER_TENANT_ID, DEFAULT_TENANT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsBytes(invalidUser)))
                .andExpect(status().isBadRequest());

        Optional<User> user = userRepository.findOneByEmailIgnoreCase("bob");
        assertThat(user).isEmpty();
    }

    private static ManagedUserVM createInvalidUser(
            String password, String firstName, String lastName, String email, boolean activated) {
        ManagedUserVM invalidUser = new ManagedUserVM();
        invalidUser.setPassword(password);
        invalidUser.setFirstName(firstName);
        invalidUser.setLastName(lastName);
        invalidUser.setEmail(email);
        invalidUser.setActivated(activated);
        invalidUser.setImageUrl("http://placehold.it/50x50");
        invalidUser.setLangKey(Constants.DEFAULT_LANGUAGE);
        invalidUser.setAuthorities(
                Collections.singleton(new AuthorityDTO(AuthoritiesConstants.USER, "User")));
        return invalidUser;
    }

    @Test
    @Transactional
    void testRegisterDuplicateEmail() throws Exception {
        // First user
        ManagedUserVM firstUser = new ManagedUserVM();
        firstUser.setPassword("password");
        firstUser.setFirstName("Alice");
        firstUser.setLastName("Test");
        firstUser.setEmail("test-register-duplicate-email@example.com");
        firstUser.setImageUrl("http://placehold.it/50x50");
        firstUser.setLangKey(Constants.DEFAULT_LANGUAGE);
        firstUser.setAuthorities(
                Collections.singleton(new AuthorityDTO(AuthoritiesConstants.USER, "User")));

        // Register first user
        restAccountMockMvc
                .perform(
                        post("/api/register")
                                .header(HEADER_TENANT_ID, DEFAULT_TENANT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsBytes(firstUser)))
                .andExpect(status().isCreated());

        Optional<User> testUser1 =
                userRepository.findOneByEmailIgnoreCase(
                        "test-register-duplicate-email@example.com");
        assertThat(testUser1).isPresent();

        // Duplicate email - with uppercase email address
        ManagedUserVM userWithUpperCaseEmail = new ManagedUserVM();
        userWithUpperCaseEmail.setId(firstUser.getId());
        userWithUpperCaseEmail.setPassword(firstUser.getPassword());
        userWithUpperCaseEmail.setFirstName(firstUser.getFirstName());
        userWithUpperCaseEmail.setLastName(firstUser.getLastName());
        userWithUpperCaseEmail.setEmail("TEST-register-duplicate-email@example.com");
        userWithUpperCaseEmail.setImageUrl(firstUser.getImageUrl());
        userWithUpperCaseEmail.setLangKey(firstUser.getLangKey());
        userWithUpperCaseEmail.setAuthorities(new HashSet<>(firstUser.getAuthorities()));

        // Register third (not activated) user
        restAccountMockMvc
                .perform(
                        post("/api/register")
                                .header(HEADER_TENANT_ID, DEFAULT_TENANT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsBytes(userWithUpperCaseEmail)))
                .andExpect(status().isCreated());

        Optional<User> testUser4 =
                userRepository.findOneByEmailIgnoreCase(
                        "test-register-duplicate-email@example.com");
        assertThat(testUser4).isPresent();
        assertThat(testUser4.orElseThrow().getEmail())
                .isEqualTo("test-register-duplicate-email@example.com");

        testUser4.orElseThrow().setActivated(true);
        userService.updateUser(userMapper.userToUserDTO(testUser4.orElseThrow()));

        userService.deleteUserByEmail("test-register-duplicate-email@example.com");
    }

    @Test
    @Transactional
    void testRegisterAdminIsIgnored() throws Exception {
        ManagedUserVM validUser = new ManagedUserVM();
        validUser.setPassword("password");
        validUser.setFirstName("Bad");
        validUser.setLastName("Guy");
        validUser.setEmail("badguy@example.com");
        validUser.setActivated(true);
        validUser.setImageUrl("http://placehold.it/50x50");
        validUser.setLangKey(Constants.DEFAULT_LANGUAGE);
        validUser.setAuthorities(
                Collections.singleton(new AuthorityDTO(AuthoritiesConstants.USER)));

        restAccountMockMvc
                .perform(
                        post("/api/register")
                                .header(HEADER_TENANT_ID, DEFAULT_TENANT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsBytes(validUser)))
                .andExpect(status().isCreated());

        Optional<User> userDup =
                userRepository.findOneWithAuthoritiesByEmailIgnoreCase("badguy@example.com");
        assertThat(userDup).isPresent();
        assertThat(userDup.orElseThrow().getAuthorities())
                .hasSize(1)
                .containsExactly(
                        authorityRepository.findById(AuthoritiesConstants.USER).orElseThrow());

        userService.deleteUserByEmail("badguy@example.com");
    }

    @Test
    @Transactional
    void testActivateAccount() throws Exception {
        final String activationKey = "some activation key";
        User user = new User();
        user.setEmail("activate-account@example.com");
        user.setPassword(RandomStringUtils.randomAlphanumeric(60));
        user.setActivated(false);
        user.setActivationKey(activationKey);

        userRepository.saveAndFlush(user);

        restAccountMockMvc
                .perform(
                        get("/api/activate?key={activationKey}", activationKey)
                                .header(HEADER_TENANT_ID, DEFAULT_TENANT))
                .andExpect(status().isOk());

        user = userRepository.findOneByEmailIgnoreCase(user.getEmail()).orElse(null);
        assertThat(user.isActivated()).isTrue();

        userService.deleteUserByEmail("activate-account@example.com");
    }

    @Test
    @Transactional
    void testActivateAccountWithWrongKey() throws Exception {
        restAccountMockMvc
                .perform(
                        get("/api/activate?key=wrongActivationKey")
                                .header(HEADER_TENANT_ID, DEFAULT_TENANT))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @Transactional
    @WithMockUser("save-account@example.com")
    void testSaveAccountWithExistingEmail() throws Exception {
        User user = new User();
        user.setEmail("save-account@example.com");
        user.setPassword(RandomStringUtils.randomAlphanumeric(60));
        user.setActivated(true);
        userRepository.saveAndFlush(user);

        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("firstname");
        userDTO.setLastName("lastname");
        userDTO.setEmail("save-account@example.com");
        userDTO.setActivated(false);
        userDTO.setImageUrl("http://placehold.it/50x50");
        userDTO.setLangKey(Constants.DEFAULT_LANGUAGE);
        userDTO.setAuthorities(Collections.singleton(new AuthorityDTO(AuthoritiesConstants.ADMIN)));

        restAccountMockMvc
                .perform(
                        post("/api/account")
                                .header(HEADER_TENANT_ID, DEFAULT_TENANT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsBytes(userDTO)))
                .andExpect(status().is(200));

        User updatedUser =
                userRepository
                        .findOneWithAuthoritiesByEmailIgnoreCase(user.getEmail())
                        .orElse(null);
        assertThat(updatedUser.getFirstName()).isEqualTo(userDTO.getFirstName());
        assertThat(updatedUser.getLastName()).isEqualTo(userDTO.getLastName());
        assertThat(updatedUser.getEmail()).isEqualTo(userDTO.getEmail());
        assertThat(updatedUser.getLangKey()).isEqualTo(userDTO.getLangKey());
        assertThat(updatedUser.getPassword()).isEqualTo(user.getPassword());
        assertThat(updatedUser.getImageUrl()).isEqualTo(userDTO.getImageUrl());
        assertThat(updatedUser.isActivated()).isTrue();
        assertThat(updatedUser.getAuthorities()).isEmpty();

        userService.deleteUserByEmail("save-account@example.com");
    }

    @Test
    @Transactional
    @WithMockUser("save-invalid-email@example.com")
    void testSaveInvalidEmail() throws Exception {
        User user = new User();
        user.setEmail("save-invalid-email@example.com");
        user.setPassword(RandomStringUtils.randomAlphanumeric(60));
        user.setActivated(true);

        userRepository.saveAndFlush(user);

        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("firstname");
        userDTO.setLastName("lastname");
        userDTO.setEmail("invalid email");
        userDTO.setActivated(false);
        userDTO.setImageUrl("http://placehold.it/50x50");
        userDTO.setLangKey(Constants.DEFAULT_LANGUAGE);
        userDTO.setAuthorities(Collections.singleton(new AuthorityDTO(AuthoritiesConstants.ADMIN)));

        restAccountMockMvc
                .perform(
                        post("/api/account")
                                .header(HEADER_TENANT_ID, DEFAULT_TENANT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsBytes(userDTO)))
                .andExpect(status().isBadRequest());

        assertThat(userRepository.findOneByEmailIgnoreCase("invalid email")).isNotPresent();

        userService.deleteUserByEmail("save-invalid-email@example.com");
    }

    @Test
    @Transactional
    @WithMockUser("save-existing-email@example.com")
    void testSaveExistingEmail() throws Exception {
        User user = new User();
        user.setEmail("save-existing-email@example.com");
        user.setPassword(RandomStringUtils.randomAlphanumeric(60));
        user.setActivated(true);
        userRepository.saveAndFlush(user);

        User anotherUser = new User();
        anotherUser.setEmail("save-existing-email2@example.com");
        anotherUser.setPassword(RandomStringUtils.randomAlphanumeric(60));
        anotherUser.setActivated(true);

        userRepository.saveAndFlush(anotherUser);

        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("firstname");
        userDTO.setLastName("lastname");
        userDTO.setEmail("save-existing-email2@example.com");
        userDTO.setActivated(false);
        userDTO.setImageUrl("http://placehold.it/50x50");
        userDTO.setLangKey(Constants.DEFAULT_LANGUAGE);
        userDTO.setAuthorities(Collections.singleton(new AuthorityDTO(AuthoritiesConstants.ADMIN)));

        restAccountMockMvc
                .perform(
                        post("/api/account")
                                .header(HEADER_TENANT_ID, DEFAULT_TENANT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsBytes(userDTO)))
                .andExpect(status().is(500));

        userService.deleteUserByEmail("save-existing-email@example.com");
        userService.deleteUserByEmail("save-existing-email2@example.com");
    }

    @Test
    @Transactional
    @WithMockUser("save-existing-email-and-login@example.com")
    void testSaveExistingEmailAndLogin() throws Exception {
        User user = new User();
        user.setEmail("save-existing-email-and-login@example.com");
        user.setPassword(RandomStringUtils.randomAlphanumeric(60));
        user.setActivated(true);
        userRepository.saveAndFlush(user);

        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("firstname");
        userDTO.setLastName("lastname");
        userDTO.setEmail("save-existing-email-and-login@example.com");
        userDTO.setActivated(false);
        userDTO.setImageUrl("http://placehold.it/50x50");
        userDTO.setLangKey(Constants.DEFAULT_LANGUAGE);
        userDTO.setAuthorities(Collections.singleton(new AuthorityDTO(AuthoritiesConstants.ADMIN)));

        restAccountMockMvc
                .perform(
                        post("/api/account")
                                .header(HEADER_TENANT_ID, DEFAULT_TENANT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsBytes(userDTO)))
                .andExpect(status().is(200));

        User updatedUser =
                userRepository
                        .findOneByEmailIgnoreCase("save-existing-email-and-login@example.com")
                        .orElse(null);
        assertThat(updatedUser.getEmail()).isEqualTo("save-existing-email-and-login@example.com");

        userService.deleteUserByEmail("save-existing-email-and-login@example.com");
    }

    @Test
    @Transactional
    @WithMockUser("change-password-wrong-existing-password@example.com")
    void testChangePasswordWrongExistingPassword() throws Exception {
        User user = new User();
        String currentPassword = RandomStringUtils.randomAlphanumeric(60);
        user.setPassword(passwordEncoder.encode(currentPassword));
        user.setEmail("change-password-wrong-existing-password@example.com");
        userRepository.saveAndFlush(user);

        restAccountMockMvc
                .perform(
                        post("/api/account/change-password")
                                .header(HEADER_TENANT_ID, DEFAULT_TENANT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        om.writeValueAsBytes(
                                                new PasswordChangeDTO(
                                                        "1" + currentPassword, "new password"))))
                .andExpect(status().isBadRequest());

        User updatedUser =
                userRepository
                        .findOneByEmailIgnoreCase(
                                "change-password-wrong-existing-password@example.com")
                        .orElse(null);
        assertThat(passwordEncoder.matches("new password", updatedUser.getPassword())).isFalse();
        assertThat(passwordEncoder.matches(currentPassword, updatedUser.getPassword())).isTrue();

        userService.deleteUserByEmail("change-password-wrong-existing-password@example.com");
    }

    @Test
    @Transactional
    @WithMockUser("change-password@example.com")
    void testChangePassword() throws Exception {
        User user = new User();
        String currentPassword = RandomStringUtils.randomAlphanumeric(60);
        user.setPassword(passwordEncoder.encode(currentPassword));
        user.setEmail("change-password@example.com");
        userRepository.saveAndFlush(user);

        restAccountMockMvc
                .perform(
                        post("/api/account/change-password")
                                .header(HEADER_TENANT_ID, DEFAULT_TENANT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        om.writeValueAsBytes(
                                                new PasswordChangeDTO(
                                                        currentPassword, "new password"))))
                .andExpect(status().isOk());

        User updatedUser =
                userRepository.findOneByEmailIgnoreCase("change-password@example.com").orElse(null);
        assertThat(passwordEncoder.matches("new password", updatedUser.getPassword())).isTrue();

        userService.deleteUserByEmail("change-password@example.com");
    }

    @Test
    @Transactional
    @WithMockUser("change-password-too-small@example.com")
    void testChangePasswordTooSmall() throws Exception {
        User user = new User();
        String currentPassword = RandomStringUtils.randomAlphanumeric(60);
        user.setPassword(passwordEncoder.encode(currentPassword));
        user.setEmail("change-password-too-small@example.com");
        userRepository.saveAndFlush(user);

        String newPassword = RandomStringUtils.random(ManagedUserVM.PASSWORD_MIN_LENGTH - 1);

        restAccountMockMvc
                .perform(
                        post("/api/account/change-password")
                                .header(HEADER_TENANT_ID, DEFAULT_TENANT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        om.writeValueAsBytes(
                                                new PasswordChangeDTO(
                                                        currentPassword, newPassword))))
                .andExpect(status().isBadRequest());

        User updatedUser =
                userRepository
                        .findOneByEmailIgnoreCase("change-password-too-small@example.com")
                        .orElse(null);
        assertThat(updatedUser.getPassword()).isEqualTo(user.getPassword());

        userService.deleteUserByEmail("change-password-too-small@example.com");
    }

    @Test
    @Transactional
    @WithMockUser("change-password-too-long@example.com")
    void testChangePasswordTooLong() throws Exception {
        User user = new User();
        String currentPassword = RandomStringUtils.randomAlphanumeric(60);
        user.setPassword(passwordEncoder.encode(currentPassword));
        user.setEmail("change-password-too-long@example.com");
        userRepository.saveAndFlush(user);

        String newPassword = RandomStringUtils.random(ManagedUserVM.PASSWORD_MAX_LENGTH + 1);

        restAccountMockMvc
                .perform(
                        post("/api/account/change-password")
                                .header(HEADER_TENANT_ID, DEFAULT_TENANT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        om.writeValueAsBytes(
                                                new PasswordChangeDTO(
                                                        currentPassword, newPassword))))
                .andExpect(status().isBadRequest());

        User updatedUser =
                userRepository
                        .findOneByEmailIgnoreCase("change-password-too-long@example.com")
                        .orElse(null);
        assertThat(updatedUser.getPassword()).isEqualTo(user.getPassword());

        userService.deleteUserByEmail("change-password-too-long@example.com");
    }

    @Test
    @Transactional
    @WithMockUser("change-password-empty@example.com")
    void testChangePasswordEmpty() throws Exception {
        User user = new User();
        String currentPassword = RandomStringUtils.randomAlphanumeric(60);
        user.setPassword(passwordEncoder.encode(currentPassword));
        user.setEmail("change-password-empty@example.com");
        userRepository.saveAndFlush(user);

        restAccountMockMvc
                .perform(
                        post("/api/account/change-password")
                                .header(HEADER_TENANT_ID, DEFAULT_TENANT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        om.writeValueAsBytes(
                                                new PasswordChangeDTO(currentPassword, ""))))
                .andExpect(status().isBadRequest());

        User updatedUser =
                userRepository
                        .findOneByEmailIgnoreCase("change-password-empty@example.com")
                        .orElse(null);
        assertThat(updatedUser.getPassword()).isEqualTo(user.getPassword());

        userService.deleteUserByEmail("change-password-empty@example.com");
    }

    @Test
    @Transactional
    void testRequestPasswordReset() throws Exception {
        User user = new User();
        user.setPassword(RandomStringUtils.randomAlphanumeric(60));
        user.setActivated(true);
        user.setEmail("password-reset@example.com");
        user.setLangKey("en");
        userRepository.saveAndFlush(user);

        restAccountMockMvc
                .perform(
                        post("/api/account/reset-password/init")
                                .header(HEADER_TENANT_ID, DEFAULT_TENANT)
                                .content("password-reset@example.com"))
                .andExpect(status().isOk());

        userService.deleteUserByEmail("password-reset@example.com");
    }

    @Test
    @Transactional
    void testRequestPasswordResetUpperCaseEmail() throws Exception {
        User user = new User();
        user.setPassword(RandomStringUtils.randomAlphanumeric(60));
        user.setActivated(true);
        user.setEmail("password-reset-upper-case@example.com");
        user.setLangKey("en");
        userRepository.saveAndFlush(user);

        restAccountMockMvc
                .perform(
                        post("/api/account/reset-password/init")
                                .header(HEADER_TENANT_ID, DEFAULT_TENANT)
                                .content("password-reset-upper-case@EXAMPLE.COM"))
                .andExpect(status().isOk());

        userService.deleteUserByEmail("password-reset-upper-case@example.com");
    }

    @Test
    void testRequestPasswordResetWrongEmail() throws Exception {
        restAccountMockMvc
                .perform(
                        post("/api/account/reset-password/init")
                                .header(HEADER_TENANT_ID, DEFAULT_TENANT)
                                .content("password-reset-wrong-email@example.com"))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    void testFinishPasswordReset() throws Exception {
        User user = new User();
        user.setPassword(RandomStringUtils.randomAlphanumeric(60));
        user.setEmail("finish-password-reset@example.com");
        user.setResetDate(Instant.now().plusSeconds(60));
        user.setResetKey("reset key");
        userRepository.saveAndFlush(user);

        KeyAndPasswordVM keyAndPassword = new KeyAndPasswordVM();
        keyAndPassword.setKey(user.getResetKey());
        keyAndPassword.setNewPassword("new password");

        restAccountMockMvc
                .perform(
                        post("/api/account/reset-password/finish")
                                .header(HEADER_TENANT_ID, DEFAULT_TENANT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsBytes(keyAndPassword)))
                .andExpect(status().isOk());

        User updatedUser = userRepository.findOneByEmailIgnoreCase(user.getEmail()).orElse(null);
        assertThat(
                        passwordEncoder.matches(
                                keyAndPassword.getNewPassword(), updatedUser.getPassword()))
                .isTrue();

        userService.deleteUserByEmail("finish-password-reset@example.com");
    }

    @Test
    @Transactional
    void testFinishPasswordResetTooSmall() throws Exception {
        User user = new User();
        user.setPassword(RandomStringUtils.randomAlphanumeric(60));
        user.setEmail("finish-password-reset-too-small@example.com");
        user.setResetDate(Instant.now().plusSeconds(60));
        user.setResetKey("reset key too small");
        userRepository.saveAndFlush(user);

        KeyAndPasswordVM keyAndPassword = new KeyAndPasswordVM();
        keyAndPassword.setKey(user.getResetKey());
        keyAndPassword.setNewPassword("foo");

        restAccountMockMvc
                .perform(
                        post("/api/account/reset-password/finish")
                                .header(HEADER_TENANT_ID, DEFAULT_TENANT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsBytes(keyAndPassword)))
                .andExpect(status().isBadRequest());

        User updatedUser = userRepository.findOneByEmailIgnoreCase(user.getEmail()).orElse(null);
        assertThat(
                        passwordEncoder.matches(
                                keyAndPassword.getNewPassword(), updatedUser.getPassword()))
                .isFalse();

        userService.deleteUserByEmail("finish-password-reset-too-small@example.com");
    }

    @Test
    @Transactional
    void testFinishPasswordResetWrongKey() throws Exception {
        KeyAndPasswordVM keyAndPassword = new KeyAndPasswordVM();
        keyAndPassword.setKey("wrong reset key");
        keyAndPassword.setNewPassword("new password");

        restAccountMockMvc
                .perform(
                        post("/api/account/reset-password/finish")
                                .header(HEADER_TENANT_ID, DEFAULT_TENANT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsBytes(keyAndPassword)))
                .andExpect(status().isInternalServerError());
    }
}
