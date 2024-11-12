package io.flexwork.modules.usermanagement.web.rest;

import io.flexwork.modules.usermanagement.domain.Authority;
import io.flexwork.modules.usermanagement.repository.AuthorityRepository;
import io.flexwork.modules.usermanagement.service.UserService;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

@RestController
@RequestMapping("/api/authorities")
@Transactional
public class AuthorityController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthorityController.class);

    private static final String ENTITY_NAME = "adminAuthority";

    @Value("${spring.application.name}")
    private String applicationName;

    private final AuthorityRepository authorityRepository;

    private final UserService userService;

    public AuthorityController(AuthorityRepository authorityRepository, UserService userService) {
        this.authorityRepository = authorityRepository;
        this.userService = userService;
    }

    /**
     * {@code POST /authorities} : Create a new authority.
     *
     * @param authority the authority to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new
     *     authority, or with status {@code 400 (Bad Request)} if the authority has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<Authority> createAuthority(@Valid @RequestBody Authority authority)
            throws URISyntaxException {
        LOG.debug("REST request to save Authority : {}", authority);

        authority = authorityRepository.save(authority);
        return ResponseEntity.created(new URI("/api/authorities/" + authority.getName()))
                .headers(
                        HeaderUtil.createEntityCreationAlert(
                                applicationName, true, ENTITY_NAME, authority.getName()))
                .body(authority);
    }

    /**
     * {@code GET /authorities} : get all the authorities.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of authorities
     *     in body.
     */
    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public Page<Authority> getAllAuthorities(Pageable pageable) {
        LOG.debug("REST request to get all Authorities");
        return authorityRepository.findAll(pageable);
    }

    /**
     * {@code GET /authorities/:id} : get the "id" authority.
     *
     * @param name the id of the authority to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the authority,
     *     or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{name}")
    public ResponseEntity<Authority> getAuthority(@PathVariable("name") String name) {
        Optional<Authority> authority = authorityRepository.findByName(name);
        return ResponseUtil.wrapOrNotFound(authority);
    }

    /**
     * {@code DELETE /authorities/:id} : delete the "id" authority.
     *
     * @param id the id of the authority to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteAuthority(@PathVariable("id") String id) {
        LOG.debug("REST request to delete Authority : {}", id);
        authorityRepository.deleteById(id);
        return ResponseEntity.noContent()
                .headers(
                        HeaderUtil.createEntityDeletionAlert(
                                applicationName, true, ENTITY_NAME, id))
                .build();
    }

    @PostMapping("/{authorityName}/add-users")
    public ResponseEntity<Void> addUsersToAuthority(
            @PathVariable String authorityName, @RequestBody List<Long> userIds) {
        userService.addUsersToAuthority(userIds, authorityName);
        return ResponseEntity.ok().build();
    }
}
