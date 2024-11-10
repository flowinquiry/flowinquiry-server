package io.flexwork.modules.usermanagement.web.rest;

import io.flexwork.modules.usermanagement.service.AuthorityResourcePermissionService;
import io.flexwork.modules.usermanagement.service.dto.AuthorityResourcePermissionDTO;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/authority-permissions")
public class AuthorityResourcePermissionController {

    private final AuthorityResourcePermissionService authorityResourcePermissionService;

    @Autowired
    public AuthorityResourcePermissionController(
            AuthorityResourcePermissionService authorityResourcePermissionService) {
        this.authorityResourcePermissionService = authorityResourcePermissionService;
    }

    /**
     * Endpoint to get all resource permissions associated with a specific authority.
     *
     * @param authorityName The name of the authority.
     * @return A list of AuthorityResourcePermission objects.
     */
    @GetMapping("/{authorityName}")
    public List<AuthorityResourcePermissionDTO> getPermissionsByAuthority(
            @PathVariable String authorityName) {
        return authorityResourcePermissionService.getResourcePermissionsByAuthority(authorityName);
    }

    @PostMapping("/batchSave")
    public List<AuthorityResourcePermissionDTO> savePermissions(
            @RequestBody List<AuthorityResourcePermissionDTO> permissions) {
        return authorityResourcePermissionService.saveAllPermissions(permissions);
    }
}
