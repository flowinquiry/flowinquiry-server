package io.flexwork.modules.collab.service.mapper;

import io.flexwork.modules.collab.domain.Organization;
import io.flexwork.modules.collab.domain.Team;
import io.flexwork.modules.collab.service.dto.TeamDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TeamMapper {

    @Mapping(target = "organizationId", source = "organization.id")
    TeamDTO toDto(Team team);

    @Mapping(
            target = "organization",
            expression = "java(ofOrganization(teamDTO.getOrganizationId()))")
    Team toEntity(TeamDTO teamDTO);

    @Mapping(
            target = "organization",
            expression = "java(ofOrganization(teamDTO.getOrganizationId()))")
    void updateFromDto(TeamDTO teamDTO, @MappingTarget Team team);

    default Organization ofOrganization(Long organizationId) {
        return (organizationId == null) ? null : Organization.builder().id(organizationId).build();
    }
}