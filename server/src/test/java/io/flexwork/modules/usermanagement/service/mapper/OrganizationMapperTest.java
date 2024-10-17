package io.flexwork.modules.usermanagement.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.flexwork.modules.usermanagement.domain.Organization;
import io.flexwork.modules.usermanagement.domain.Team;
import io.flexwork.modules.usermanagement.service.dto.OrganizationDTO;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class OrganizationMapperTest {
    private OrganizationMapper organizationMapper = Mappers.getMapper(OrganizationMapper.class);

    @Test
    void testOrganizationToOrganizationDTO() {
        Organization organization =
                Organization.builder()
                        .id(1L)
                        .description("description")
                        .teams(
                                Set.of(
                                        Team.builder().id(1L).name("Team1").build(),
                                        Team.builder().id(2L).name("Team2").build()))
                        .build();
        OrganizationDTO organizationDTO =
                organizationMapper.organizationToOrganizationDTO(organization);
        assertAll(
                () -> assertEquals(organization.getId(), organizationDTO.getId()),
                () -> assertEquals(organization.getDescription(), organizationDTO.getDescription()),
                () -> assertEquals(2, organizationDTO.getTeams().size()),
                () ->
                        assertThat(organizationDTO.getTeams())
                                .extracting("id")
                                .containsAnyOf(1L, 2L));
    }
}
