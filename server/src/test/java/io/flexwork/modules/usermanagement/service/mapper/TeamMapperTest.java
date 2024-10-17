package io.flexwork.modules.usermanagement.service.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.flexwork.modules.usermanagement.domain.Organization;
import io.flexwork.modules.usermanagement.domain.Team;
import io.flexwork.modules.usermanagement.service.dto.TeamDTO;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class TeamMapperTest {

    private TeamMapper teamMapper = Mappers.getMapper(TeamMapper.class);

    @Test
    public void testTeamDTOToTeam() {
        TeamDTO teamDTO = TeamDTO.builder().id(1L).name("Name").organizationId(1L).build();
        Team team = teamMapper.teamDTOToTeam(teamDTO);
        assertAll(
                () -> assertEquals(teamDTO.getId(), team.getId()),
                () -> assertEquals(teamDTO.getName(), team.getName()),
                () -> assertEquals(teamDTO.getOrganizationId(), team.getOrganization().getId()));
    }

    @Test
    public void testTeamToTeamDTO() {
        Team team =
                Team.builder()
                        .id(1L)
                        .slogan("Slogan")
                        .organization(
                                Organization.builder().id(1L).description("description").build())
                        .build();
        TeamDTO teamDTO = teamMapper.teamToTeamDTO(team);
        assertAll(
                () -> assertEquals(team.getId(), teamDTO.getId()),
                () -> assertEquals(team.getOrganization().getId(), teamDTO.getId()),
                () -> assertEquals(team.getSlogan(), teamDTO.getSlogan()));
    }
}
