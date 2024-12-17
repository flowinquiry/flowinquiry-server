package io.flowinquiry.modules.usermanagement.service.mapper;

import io.flowinquiry.modules.usermanagement.domain.Authority;
import io.flowinquiry.modules.usermanagement.service.dto.AuthorityDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthorityMapper {

    AuthorityDTO toDto(Authority authority);

    Authority toEntity(AuthorityDTO authorityDTO);
}
