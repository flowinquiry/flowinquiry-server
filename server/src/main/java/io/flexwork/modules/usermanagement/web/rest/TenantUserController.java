package io.flexwork.modules.usermanagement.web.rest;

import io.flexwork.db.TenantConstants;
import io.flexwork.modules.usermanagement.service.UserService;
import io.flexwork.modules.usermanagement.service.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tenants")
public class TenantUserController {

    private static final Logger log = LoggerFactory.getLogger(TenantUserController.class);

    private final UserService userService;

    public TenantUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public Page<UserDTO> getUsers(Pageable pageable) {
        return userService.getAllPublicUsers(pageable);
    }

    @PostMapping("/users")
    public void createUser(
            @RequestHeader(value = TenantConstants.HEADER_TENANT_ID) String tenantId,
            @RequestBody UserDTO userDTO) {
        log.debug("REST request to save User: {} for tenant {}", userDTO, tenantId);
        //        userService.saveUser(UserMapperClassic.instance.userDtoToUser(userDTO));
    }
}
