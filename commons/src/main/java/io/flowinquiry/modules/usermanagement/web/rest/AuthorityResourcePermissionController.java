package io.flowinquiry.modules.usermanagement.web.rest;

import io.flowinquiry.modules.usermanagement.service.AuthorityResourcePermissionService;
import io.flowinquiry.modules.usermanagement.service.dto.AuthorityResourcePermissionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
