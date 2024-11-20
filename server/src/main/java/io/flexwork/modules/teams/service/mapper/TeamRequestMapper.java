package io.flexwork.modules.teams.service.mapper;

import io.flexwork.modules.teams.domain.TeamRequest;
import io.flexwork.modules.teams.service.dto.TeamRequestDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface TeamRequestMapper {

    TeamRequestDTO toDto(TeamRequest teamRequest);

    TeamRequest toEntity(TeamRequestDTO teamRequestDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(TeamRequestDTO dto, @MappingTarget TeamRequest entity);
}
