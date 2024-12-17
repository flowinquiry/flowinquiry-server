package io.flowinquiry.modules.usermanagement.service.mapper;

import io.flowinquiry.modules.usermanagement.domain.Tenant;
import io.flowinquiry.modules.usermanagement.service.dto.TenantDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TenantMapper {

    Tenant toEntity(TenantDTO tenantDTO);

    TenantDTO toDto(Tenant tenant);
}
