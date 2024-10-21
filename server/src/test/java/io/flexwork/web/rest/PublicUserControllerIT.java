package io.flexwork.web.rest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.flexwork.DefaultTenantContext;
import io.flexwork.IntegrationTest;
import io.flexwork.modules.usermanagement.AuthoritiesConstants;
import io.flexwork.modules.usermanagement.domain.User;
import io.flexwork.modules.usermanagement.repository.UserRepository;
import io.flexwork.modules.usermanagement.service.UserService;
import io.flexwork.modules.usermanagement.web.rest.PublicUserController;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/** Integration tests for the {@link PublicUserController} REST controller. */
@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
@IntegrationTest
@DefaultTenantContext
class PublicUserControllerIT {

    @Autowired private UserRepository userRepository;

    @Autowired private UserService userService;

    @Autowired private EntityManager em;

    @Autowired private MockMvc restUserMockMvc;

    private User user;
    private Long numberOfUsers;

    @BeforeEach
    public void countUsers() {
        numberOfUsers = userRepository.count();
    }

    @BeforeEach
    public void initTest() {
        user = UserControllerIT.initTestUser(em);
    }

    @AfterEach
    public void cleanupAndCheck() {
        userService.deleteUserByEmail(user.getEmail());
        assertThat(userRepository.count()).isEqualTo(numberOfUsers);
        numberOfUsers = null;
    }

    @Test
    @Transactional
    void searchAllPublicUsers() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);

        // Get all the users
        restUserMockMvc
                .perform(post("/api/users/search?sort=id,desc").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(
                        jsonPath("$.content[?(@.id == %d)].email", user.getId())
                                .value(user.getEmail()));
    }

    @Test
    @Transactional
    void searchAllUsersSortedByParameters() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);

        restUserMockMvc
                .perform(
                        post("/api/users/search?sort=resetKey,desc")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        restUserMockMvc
                .perform(
                        post("/api/users/search?sort=password,desc")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        restUserMockMvc
                .perform(
                        post("/api/users/search?sort=resetKey,desc&sort=id,desc")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        restUserMockMvc
                .perform(post("/api/users/search?sort=id,desc").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
