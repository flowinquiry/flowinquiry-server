package io.flowinquiry.modules.usermanagement.service.mapper;

import io.flowinquiry.modules.usermanagement.domain.AuthorityResourcePermission;
import io.flowinquiry.modules.usermanagement.service.dto.AuthorityResourcePermissionDTO;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthorityResourcePermissionMapper {

    AuthorityResourcePermissionDTO toDto(AuthorityResourcePermission authorityResourcePermission);

    List<AuthorityResourcePermission> toEntityList(List<AuthorityResourcePermissionDTO> dtos);

    List<AuthorityResourcePermissionDTO> toDtoList(List<AuthorityResourcePermission> entities);
}
