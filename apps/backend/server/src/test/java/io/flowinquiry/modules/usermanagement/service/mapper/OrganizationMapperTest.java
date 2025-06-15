package io.flowinquiry.modules.usermanagement.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import io.flowinquiry.modules.teams.domain.Organization;
import io.flowinquiry.modules.teams.domain.Team;
import io.flowinquiry.modules.teams.service.dto.OrganizationDTO;
import io.flowinquiry.modules.teams.service.dto.TeamDTO;
import io.flowinquiry.modules.teams.service.mapper.OrganizationMapper;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class OrganizationMapperTest {
    private OrganizationMapper organizationMapper;

    @BeforeEach
    public void setup() {
        organizationMapper = Mappers.getMapper(OrganizationMapper.class);
    }

    @Test
    void testToDto() {
        Organization organization =
                Organization.builder()
                        .id(1L)
                        .name("Test Organization")
                        .logoUrl("http://example.com/logo.png")
                        .slogan("Test Slogan")
                        .description("description")
                        .teams(
                                Set.of(
                                        Team.builder().id(1L).name("Team1").build(),
                                        Team.builder().id(2L).name("Team2").build()))
                        .build();
        OrganizationDTO organizationDTO = organizationMapper.toDto(organization);
        assertAll(
                () -> assertEquals(organization.getId(), organizationDTO.getId()),
                () -> assertEquals(organization.getName(), organizationDTO.getName()),
                () -> assertEquals(organization.getLogoUrl(), organizationDTO.getLogoUrl()),
                () -> assertEquals(organization.getSlogan(), organizationDTO.getSlogan()),
                () -> assertEquals(organization.getDescription(), organizationDTO.getDescription()),
                () -> assertEquals(2, organizationDTO.getTeams().size()),
                () ->
                        assertThat(organizationDTO.getTeams())
                                .extracting("id")
                                .containsAnyOf(1L, 2L));
    }

    @Test
    void testToEntity() {
        // Given
        Set<TeamDTO> teamDTOs = new HashSet<>();
        teamDTOs.add(TeamDTO.builder().id(1L).name("Team1").build());
        teamDTOs.add(TeamDTO.builder().id(2L).name("Team2").build());

        OrganizationDTO organizationDTO =
                OrganizationDTO.builder()
                        .id(1L)
                        .name("Test Organization")
                        .logoUrl("http://example.com/logo.png")
                        .slogan("Test Slogan")
                        .description("description")
                        .teams(teamDTOs)
                        .build();

        // When
        Organization organization = organizationMapper.toEntity(organizationDTO);

        // Then
        assertAll(
                () -> assertEquals(organizationDTO.getId(), organization.getId()),
                () -> assertEquals(organizationDTO.getName(), organization.getName()),
                () -> assertEquals(organizationDTO.getLogoUrl(), organization.getLogoUrl()),
                () -> assertEquals(organizationDTO.getSlogan(), organization.getSlogan()),
                () ->
                        assertEquals(
                                organizationDTO.getDescription(), organization.getDescription()));
    }

    @Test
    void testNullValues() {
        // Test null entity
        assertNull(organizationMapper.toDto(null));

        // Test null DTO
        assertNull(organizationMapper.toEntity(null));
    }
}
