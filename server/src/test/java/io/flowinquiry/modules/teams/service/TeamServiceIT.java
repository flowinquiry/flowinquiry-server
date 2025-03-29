package io.flowinquiry.modules.teams.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import io.flowinquiry.IntegrationTest;
import io.flowinquiry.exceptions.ResourceNotFoundException;
import io.flowinquiry.modules.teams.service.dto.TeamDTO;
import io.flowinquiry.modules.teams.service.event.NewTeamCreatedEvent;
import io.flowinquiry.modules.teams.service.event.RemoveUserOutOfTeamEvent;
import io.flowinquiry.modules.usermanagement.service.dto.UserWithTeamRoleDTO;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
public class TeamServiceIT {

    @Autowired private TeamService teamService;

    @Autowired private ApplicationEventPublisher realEventPublisher;
    private ApplicationEventPublisher spyEventPublisher;

    @BeforeEach
    void setUp() {
        spyEventPublisher = Mockito.spy(realEventPublisher);
        ReflectionTestUtils.setField(teamService, "eventPublisher", spyEventPublisher);
        doNothing().when(spyEventPublisher).publishEvent(any());
    }

    @Test
    void shouldCreateTeamSuccessfully() {
        TeamDTO teamDTO = teamService.findTeamById(1L).orElseThrow();
        teamDTO.setId(null);
        teamDTO.setName("Flow Inquiry Team");
        teamDTO.setSlogan("Driving Innovation");
        teamDTO.setDescription("A team dedicated to workflow excellence.");

        TeamDTO savedTeam = teamService.createTeam(teamDTO);

        assertThat(savedTeam).isNotNull();
        assertThat(savedTeam.getId()).isNotNull();
        assertThat(savedTeam.getName()).isEqualTo("Flow Inquiry Team");
        assertThat(savedTeam.getSlogan()).isEqualTo("Driving Innovation");
        assertThat(savedTeam.getDescription())
                .isEqualTo("A team dedicated to workflow excellence.");
        ArgumentCaptor<NewTeamCreatedEvent> eventCaptor =
                ArgumentCaptor.forClass(NewTeamCreatedEvent.class);
        verify(spyEventPublisher, times(1)).publishEvent(eventCaptor.capture());
    }

    @Test
    void shouldUpdateTeamSuccessfully() {
        TeamDTO teamDTO = new TeamDTO();
        teamDTO.setName("Initial Team Name");
        teamDTO.setSlogan("Initial Slogan");
        teamDTO.setDescription("Initial Description");

        TeamDTO savedTeam = teamService.createTeam(teamDTO);
        Long teamId = savedTeam.getId();

        savedTeam.setName("Updated Team Name");
        savedTeam.setSlogan("Updated Slogan");
        savedTeam.setDescription("Updated Description");

        TeamDTO updatedTeam = teamService.updateTeam(savedTeam);

        // Then: Ensure values are correctly updated
        assertThat(updatedTeam).isNotNull();
        assertThat(updatedTeam.getId()).isEqualTo(teamId);
        assertThat(updatedTeam.getName()).isEqualTo("Updated Team Name");
        assertThat(updatedTeam.getSlogan()).isEqualTo("Updated Slogan");
        assertThat(updatedTeam.getDescription()).isEqualTo("Updated Description");

        // Verify the updated team in the database
        Optional<TeamDTO> fetchedTeam = teamService.findTeamById(teamId);
        assertThat(fetchedTeam).isPresent();
        assertThat(fetchedTeam.get().getName()).isEqualTo("Updated Team Name");
        assertThat(fetchedTeam.get().getSlogan()).isEqualTo("Updated Slogan");
        assertThat(fetchedTeam.get().getDescription()).isEqualTo("Updated Description");
    }

    @Test
    void shouldFailToUpdateNonExistentTeam() {
        TeamDTO nonExistentTeam = new TeamDTO();
        nonExistentTeam.setId(9999L);
        nonExistentTeam.setName("Non-Existent Team");

        assertThatThrownBy(() -> teamService.updateTeam(nonExistentTeam))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Team not found with id: 9999");
    }

    @Test
    void shouldGetUsersByTeamSuccessfully() {
        assertThat(teamService.getUsersByTeam(1L)).hasSize(12);
        teamService.addUsersToTeam(List.of(13L, 14L), "Member", 1L);

        List<UserWithTeamRoleDTO> usersByTeam = teamService.getUsersByTeam(1L);

        assertThat(usersByTeam).hasSize(14);
    }

    @Test
    void shouldReturnEmptyListWhenNoUsersInTeam() {
        TeamDTO teamDTO = new TeamDTO();
        teamDTO.setName("Empty Team");
        teamDTO.setSlogan("No members yet");
        teamDTO.setDescription("A test team without members");

        TeamDTO savedTeam = teamService.createTeam(teamDTO);
        Long teamId = savedTeam.getId();

        List<UserWithTeamRoleDTO> usersByTeam = teamService.getUsersByTeam(teamId);

        assertThat(usersByTeam).isEmpty();
    }

    @Test
    void shouldRemoveUserFromTeamSuccessfully() {
        assertThat(teamService.getUsersByTeam(1L)).hasSize(12);
        teamService.removeUserFromTeam(1L, 1L);
        assertThat(teamService.getUsersByTeam(1L)).hasSize(11);

        ArgumentCaptor<RemoveUserOutOfTeamEvent> eventCaptor =
                ArgumentCaptor.forClass(RemoveUserOutOfTeamEvent.class);
        verify(spyEventPublisher, times(1)).publishEvent(eventCaptor.capture());
    }

    @Test
    void shouldThrowExceptionWhenRemovingUserFromNonExistentTeam() {
        assertThatThrownBy(() -> teamService.removeUserFromTeam(1L, 9999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Team not found");
    }

    @Test
    void shouldBeSilentIfUserNotBelongInTeam() {
        assertThat(teamService.getUsersByTeam(1L)).hasSize(12);
        teamService.removeUserFromTeam(15L, 1L);
        assertThat(teamService.getUsersByTeam(1L)).hasSize(12);
        ArgumentCaptor<RemoveUserOutOfTeamEvent> eventCaptor =
                ArgumentCaptor.forClass(RemoveUserOutOfTeamEvent.class);
        verify(spyEventPublisher, times(0)).publishEvent(eventCaptor.capture());
    }

    @Test
    void shouldReturnCorrectUserRoleInTeam() {
        assertThat(teamService.getUserRoleInTeam(1L, 1L)).isEqualTo("Guest");
        assertThat(teamService.getUserRoleInTeam(3L, 1L)).isEqualTo("Member");
        assertThat(teamService.getUserRoleInTeam(7L, 1L)).isEqualTo("Manager");
        assertThat(teamService.getUserRoleInTeam(15L, 1L)).isEqualTo("Guest");
    }
}
