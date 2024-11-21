package io.flexwork.modules.collab.service.mapper;

import io.flexwork.modules.collab.domain.Organization;
import io.flexwork.modules.collab.domain.Team;
import io.flexwork.modules.collab.service.dto.OrganizationDTO;
import io.flexwork.modules.collab.service.dto.TeamDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrganizationMapper {

    @Mapping(target = "teams", expression = "java(mapTeamsForIdOnly(organization.getTeams()))")
    OrganizationDTO toDto(Organization organization);

    Organization toEntity(OrganizationDTO organizationDTO);

    default Set<TeamDTO> mapTeamsForIdOnly(Set<Team> teams) {
        return teams.stream()
                .map(
                        team ->
                                TeamDTO.builder()
                                        .id(team.getId())
                                        .build()) // Map only the 'id' field, set other fields as
                // null
                .collect(Collectors.toSet());
    }
}
