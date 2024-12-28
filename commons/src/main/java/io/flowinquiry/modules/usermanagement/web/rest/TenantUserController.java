package io.flowinquiry.modules.usermanagement.web.rest;

import io.flowinquiry.db.TenantConstants;
import io.flowinquiry.modules.usermanagement.service.UserService;
import io.flowinquiry.modules.usermanagement.service.dto.UserDTO;
import io.flowinquiry.modules.usermanagement.service.mapper.UserMapper;
import io.flowinquiry.query.QueryDTO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/tenants")
public class TenantUserController {

    private static final Logger LOG = LoggerFactory.getLogger(TenantUserController.class);

    private final UserService userService;

    public TenantUserController( UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users/search")
    public Page<UserDTO> getUsers(
            @Valid @RequestBody Optional<QueryDTO> queryDTO, Pageable pageable) {
        return userService.findAllPublicUsers(queryDTO, pageable);
    }

    @PostMapping("/users")
    public void createUser(
            @RequestHeader(value = TenantConstants.HEADER_TENANT_ID) String tenantId,
            @RequestBody UserDTO userDTO) {
        LOG.debug("REST request to save User: {} for tenant {}", userDTO, tenantId);
        //        userService.saveUser(UserMapperClassic.instance.userDtoToUser(userDTO));
    }
}
