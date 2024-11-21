package io.flexwork.modules.usermanagement.service.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.flexwork.modules.collab.domain.Organization;
import io.flexwork.modules.collab.domain.Team;
import io.flexwork.modules.collab.service.dto.TeamDTO;
import io.flexwork.modules.collab.service.mapper.TeamMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class TeamMapperTest {

    private TeamMapper teamMapper = Mappers.getMapper(TeamMapper.class);

    @Test
    public void testToEntity() {
        TeamDTO teamDTO = TeamDTO.builder().id(1L).name("Name").organizationId(1L).build();
        Team team = teamMapper.toEntity(teamDTO);
        assertAll(
                () -> assertEquals(teamDTO.getId(), team.getId()),
                () -> assertEquals(teamDTO.getName(), team.getName()),
                () -> assertEquals(teamDTO.getOrganizationId(), team.getOrganization().getId()));
    }

    @Test
    public void testToDto() {
        Team team =
                Team.builder()
                        .id(1L)
                        .slogan("Slogan")
                        .organization(
                                Organization.builder().id(1L).description("description").build())
                        .build();
        TeamDTO teamDTO = teamMapper.toDto(team);
        assertAll(
                () -> assertEquals(team.getId(), teamDTO.getId()),
                () -> assertEquals(team.getOrganization().getId(), teamDTO.getId()),
                () -> assertEquals(team.getSlogan(), teamDTO.getSlogan()));
    }
}
