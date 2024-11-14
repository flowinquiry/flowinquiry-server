package io.flexwork.modules.usermanagement.web.rest;

import io.flexwork.modules.usermanagement.service.UserService;
import io.flexwork.modules.usermanagement.service.dto.ResourcePermissionDTO;
import io.flexwork.modules.usermanagement.service.dto.UserDTO;
import io.flexwork.query.QueryDTO;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

@RestController
@RequestMapping("/api/users")
public class PublicUserController {

    private static final List<String> ALLOWED_ORDERED_PROPERTIES =
            List.of("id", "login", "firstName", "lastName", "email", "activated", "langKey");

    private static final Logger LOG = LoggerFactory.getLogger(PublicUserController.class);

    private final UserService userService;

    public PublicUserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * {@code GET /users} : get all users with only public information - calling this method is
     * allowed for anyone.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all users.
     */
    @PostMapping("/search")
    public ResponseEntity<Page<UserDTO>> searchAllPublicUsers(
            @Valid @RequestBody Optional<QueryDTO> queryDTO, Pageable pageable) {
        LOG.debug("REST request to get all public User names");
        if (!onlyContainsAllowedProperties(pageable)) {
            return ResponseEntity.badRequest().build();
        }

        final Page<UserDTO> page = userService.findAllPublicUsers(queryDTO, pageable);
        HttpHeaders headers =
                PaginationUtil.generatePaginationHttpHeaders(
                        ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(page, headers, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUser(@PathVariable("userId") Long userId) {
        return ResponseUtil.wrapOrNotFound(userService.getUserWithAuthoritiesById(userId));
    }

    @GetMapping("/authorities/{authorityName}")
    public ResponseEntity<List<UserDTO>> getUsersByAuthority(@PathVariable String authorityName) {
        List<UserDTO> users = userService.findAllUsersByAuthority(authorityName);

        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(users);
    }

    @GetMapping("/authorities/searchUsersNotInAuthority")
    public ResponseEntity<List<UserDTO>> findUsersNotInAuthority(
            @RequestParam("userTerm") String searchTerm,
            @RequestParam("authorityName") String authorityName) {
        PageRequest pageRequest = PageRequest.of(0, 20); // Limit to 20 results
        List<UserDTO> users =
                userService.findUsersNotInAuthority(searchTerm, authorityName, pageRequest);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/permissions/{userId}")
    public ResponseEntity<List<ResourcePermissionDTO>> getUserResourcesWithPermissions(
            @PathVariable Long userId) {
        List<ResourcePermissionDTO> resourcesWithPermissions =
                userService.getResourcesWithPermissionsByUserId(userId);
        return ResponseEntity.ok(resourcesWithPermissions);
    }

    private boolean onlyContainsAllowedProperties(Pageable pageable) {
        return pageable.getSort().stream()
                .map(Sort.Order::getProperty)
                .allMatch(ALLOWED_ORDERED_PROPERTIES::contains);
    }
}
