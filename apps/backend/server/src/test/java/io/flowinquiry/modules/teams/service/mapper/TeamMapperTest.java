package io.flowinquiry.modules.teams.service.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.flowinquiry.modules.teams.domain.Organization;
import io.flowinquiry.modules.teams.domain.Team;
import io.flowinquiry.modules.teams.service.dto.TeamDTO;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class TeamMapperTest {

    private TeamMapper teamMapper = Mappers.getMapper(TeamMapper.class);

    @Test
    public void testToDto() {
        // Given
        Organization organization = Organization.builder().id(1L).name("Org Name").build();

        Team team =
                Team.builder()
                        .id(1L)
                        .name("Team Name")
                        .logoUrl("https://example.com/logo.png")
                        .slogan("Team Slogan")
                        .description("Team Description")
                        .organization(organization)
                        .usersCount(5L)
                        .build();

        // When
        TeamDTO teamDTO = teamMapper.toDto(team);

        // Then
        assertAll(
                () -> assertEquals(team.getId(), teamDTO.getId()),
                () -> assertEquals(team.getName(), teamDTO.getName()),
                () -> assertEquals(team.getLogoUrl(), teamDTO.getLogoUrl()),
                () -> assertEquals(team.getSlogan(), teamDTO.getSlogan()),
                () -> assertEquals(team.getDescription(), teamDTO.getDescription()),
                () -> assertEquals(team.getOrganization().getId(), teamDTO.getOrganizationId()),
                () -> assertEquals(team.getUsersCount(), teamDTO.getUsersCount()));
    }

    @Test
    public void testToEntity() {
        // Given
        TeamDTO teamDTO =
                TeamDTO.builder()
                        .id(1L)
                        .name("Team Name")
                        .logoUrl("https://example.com/logo.png")
                        .slogan("Team Slogan")
                        .description("Team Description")
                        .organizationId(1L)
                        .usersCount(5L)
                        .build();

        // When
        Team team = teamMapper.toEntity(teamDTO);

        // Then
        assertAll(
                () -> assertEquals(teamDTO.getId(), team.getId()),
                () -> assertEquals(teamDTO.getName(), team.getName()),
                () -> assertEquals(teamDTO.getLogoUrl(), team.getLogoUrl()),
                () -> assertEquals(teamDTO.getSlogan(), team.getSlogan()),
                () -> assertEquals(teamDTO.getDescription(), team.getDescription()),
                () -> assertEquals(teamDTO.getOrganizationId(), team.getOrganization().getId()),
                () -> assertEquals(teamDTO.getUsersCount(), team.getUsersCount()));
    }

    @Test
    public void testUpdateFromDto() {
        // Given
        TeamDTO teamDTO =
                TeamDTO.builder()
                        .id(1L)
                        .name("Updated Team Name")
                        .logoUrl("https://example.com/updated-logo.png")
                        .slogan("Updated Team Slogan")
                        .description("Updated Team Description")
                        .organizationId(2L)
                        .usersCount(10L)
                        .build();

        Team existingTeam =
                Team.builder()
                        .id(1L)
                        .name("Original Team Name")
                        .logoUrl("https://example.com/original-logo.png")
                        .slogan("Original Team Slogan")
                        .description("Original Team Description")
                        .organization(Organization.builder().id(1L).build())
                        .usersCount(5L)
                        .build();

        // When
        teamMapper.updateFromDto(teamDTO, existingTeam);

        // Then
        assertAll(
                () -> assertEquals(teamDTO.getId(), existingTeam.getId()),
                () -> assertEquals(teamDTO.getName(), existingTeam.getName()),
                () -> assertEquals(teamDTO.getLogoUrl(), existingTeam.getLogoUrl()),
                () -> assertEquals(teamDTO.getSlogan(), existingTeam.getSlogan()),
                () -> assertEquals(teamDTO.getDescription(), existingTeam.getDescription()),
                () ->
                        assertEquals(
                                teamDTO.getOrganizationId(),
                                existingTeam.getOrganization().getId()),
                () -> assertEquals(teamDTO.getUsersCount(), existingTeam.getUsersCount()));
    }
}
