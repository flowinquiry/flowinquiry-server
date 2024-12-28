package io.flowinquiry.modules.usermanagement.web.rest;

import io.flowinquiry.modules.usermanagement.domain.Authority;
import io.flowinquiry.modules.usermanagement.service.AuthorityService;
import io.flowinquiry.modules.usermanagement.service.dto.AuthorityDTO;
import io.flowinquiry.modules.usermanagement.service.dto.UserDTO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/authorities")
@Transactional
public class AuthorityController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthorityController.class);

    private static final String ENTITY_NAME = "adminAuthority";

    @Value("${spring.application.name}")
    private String applicationName;

    private final AuthorityService authorityService;

    public AuthorityController(AuthorityService authorityService) {
        this.authorityService = authorityService;
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
    public ResponseEntity<AuthorityDTO> createAuthority(@Valid @RequestBody Authority authority)
            throws URISyntaxException {
        LOG.debug("REST request to save Authority : {}", authority);

        AuthorityDTO savedAuthority = authorityService.createAuthority(authority);
        return ResponseEntity.created(new URI("/api/authorities/" + savedAuthority.getName()))
                .headers(
                        HeaderUtil.createEntityCreationAlert(
                                applicationName, true, ENTITY_NAME, savedAuthority.getName()))
                .body(savedAuthority);
    }

    /**
     * {@code GET /authorities} : get all the authorities.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of authorities
     *     in body.
     */
    @GetMapping("")
    public Page<AuthorityDTO> getAllAuthorities(Pageable pageable) {
        return authorityService.findAllAuthorities(pageable);
    }

    /**
     * {@code GET /authorities/:id} : get the "id" authority.
     *
     * @param name the id of the authority to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the authority,
     *     or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{name}")
    public ResponseEntity<AuthorityDTO> getAuthority(@PathVariable("name") String name) {
        Optional<AuthorityDTO> authority = authorityService.findAuthorityByName(name);
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
        authorityService.deleteAuthority(id);
        return ResponseEntity.noContent()
                .headers(
                        HeaderUtil.createEntityDeletionAlert(
                                applicationName, true, ENTITY_NAME, id))
                .build();
    }

    @GetMapping("/{authorityName}/users")
    public ResponseEntity<Page<UserDTO>> getUsersByAuthority(
            @PathVariable String authorityName, Pageable pageable) {
        Page<UserDTO> users = authorityService.findAllUsersByAuthority(authorityName, pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/searchUsersNotInAuthority")
    public ResponseEntity<List<UserDTO>> findUsersNotInAuthority(
            @RequestParam("userTerm") String searchTerm,
            @RequestParam("authorityName") String authorityName) {
        PageRequest pageRequest = PageRequest.of(0, 20); // Limit to 20 results
        List<UserDTO> users =
                authorityService.findUsersNotInAuthority(searchTerm, authorityName, pageRequest);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/{authorityName}/add-users")
    public ResponseEntity<Void> addUsersToAuthority(
            @PathVariable String authorityName, @RequestBody List<Long> userIds) {
        authorityService.addUsersToAuthority(userIds, authorityName);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{authorityName}/users/{userId}")
    public ResponseEntity<Void> removeUserFromAuthority(
            @PathVariable Long userId, @PathVariable String authorityName) {
        authorityService.removeUserFromAuthority(userId, authorityName);
        return ResponseEntity.noContent().build();
    }
}
