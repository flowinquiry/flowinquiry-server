package io.flexwork.modules.usermanagement.web.rest;

import com.flexwork.platform.utils.Obfuscator;
import io.flexwork.modules.fss.service.StorageService;
import io.flexwork.modules.usermanagement.domain.User;
import io.flexwork.modules.usermanagement.repository.UserRepository;
import io.flexwork.modules.usermanagement.service.UserService;
import io.flexwork.modules.usermanagement.service.dto.ResourcePermissionDTO;
import io.flexwork.modules.usermanagement.service.dto.UserDTO;
import io.flexwork.modules.usermanagement.web.rest.errors.EmailAlreadyUsedException;
import io.flexwork.modules.usermanagement.web.rest.errors.LoginAlreadyUsedException;
import io.flexwork.query.QueryDTO;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

@RestController
@RequestMapping("/api/users")
public class PublicUserController {

    private static final List<String> ALLOWED_ORDERED_PROPERTIES =
            List.of("id", "login", "firstName", "lastName", "email", "activated", "langKey");

    private static final Logger LOG = LoggerFactory.getLogger(PublicUserController.class);

    private final UserRepository userRepository;
    private final UserService userService;
    private final StorageService storageService;

    public PublicUserController(
            UserService userService, UserRepository userRepository, StorageService storageService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.storageService = storageService;
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

    /**
     * {@code PUT /users} : Updates an existing User.
     *
     * @param userDTO the user to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated
     *     user.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already in use.
     * @throws LoginAlreadyUsedException {@code 400 (Bad Request)} if the email is already in use.
     */
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDTO> updateUser(
            @RequestPart("userDTO") UserDTO userDTO,
            @RequestParam(value = "file", required = false) MultipartFile avatarFile)
            throws Exception {
        Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
        if (existingUser.isPresent()
                && (!existingUser.orElseThrow().getId().equals(userDTO.getId()))) {
            throw new EmailAlreadyUsedException();
        }
        existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail().toLowerCase());
        if (existingUser.isPresent()
                && (!existingUser.orElseThrow().getId().equals(userDTO.getId()))) {
            throw new LoginAlreadyUsedException();
        }

        // Handle the avatar file upload, if present
        if (avatarFile != null && !avatarFile.isEmpty()) {
            String avatarPath =
                    storageService.uploadImage(
                            "avatar",
                            Obfuscator.obfuscate(userDTO.getId()),
                            avatarFile.getInputStream());
            userDTO.setImageUrl(avatarPath);
        }

        UserDTO updatedUser = userService.updateUser(userDTO);

        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUser(@PathVariable("userId") Long userId) {
        return ResponseUtil.wrapOrNotFound(userService.getUserWithManagerById(userId));
    }

    @GetMapping("/permissions/{userId}")
    public ResponseEntity<List<ResourcePermissionDTO>> getUserResourcesWithPermissions(
            @PathVariable Long userId) {
        List<ResourcePermissionDTO> resourcesWithPermissions =
                userService.getResourcesWithPermissionsByUserId(userId);
        return ResponseEntity.ok(resourcesWithPermissions);
    }

    @GetMapping("/{managerId}/direct-reports")
    public ResponseEntity<List<UserDTO>> getDirectReports(@PathVariable Long managerId) {
        List<UserDTO> directReports = userService.getDirectReports(managerId);
        return ResponseEntity.ok(directReports);
    }

    private boolean onlyContainsAllowedProperties(Pageable pageable) {
        return pageable.getSort().stream()
                .map(Sort.Order::getProperty)
                .allMatch(ALLOWED_ORDERED_PROPERTIES::contains);
    }
}
