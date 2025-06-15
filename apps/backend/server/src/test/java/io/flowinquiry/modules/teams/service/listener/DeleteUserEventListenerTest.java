package io.flowinquiry.modules.teams.service.listener;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.flowinquiry.modules.teams.service.TeamService;
import io.flowinquiry.modules.teams.service.dto.TeamDTO;
import io.flowinquiry.modules.usermanagement.service.event.DeleteUserEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DeleteUserEventListenerTest {

    @Mock private TeamService teamService;

    private DeleteUserEventListener deleteUserEventListener;

    @BeforeEach
    public void setup() {
        deleteUserEventListener = new DeleteUserEventListener(teamService);
    }

    @Test
    public void testOnDeleteUserEvent_WithNoTeams() {
        // Given
        Long userId = 1L;
        DeleteUserEvent event = new DeleteUserEvent(this, userId);

        when(teamService.findAllTeamsByUserId(userId)).thenReturn(Collections.emptyList());

        // When
        deleteUserEventListener.onDeleteUserEvent(event);

        // Then
        verify(teamService).findAllTeamsByUserId(userId);
        verify(teamService, times(0)).removeUserFromTeam(userId, null);
    }

    @Test
    public void testOnDeleteUserEvent_WithOneTeam() {
        // Given
        Long userId = 1L;
        Long teamId = 10L;
        DeleteUserEvent event = new DeleteUserEvent(this, userId);

        TeamDTO team = new TeamDTO();
        team.setId(teamId);

        when(teamService.findAllTeamsByUserId(userId)).thenReturn(Collections.singletonList(team));

        // When
        deleteUserEventListener.onDeleteUserEvent(event);

        // Then
        verify(teamService).findAllTeamsByUserId(userId);
        verify(teamService).removeUserFromTeam(userId, teamId);
    }

    @Test
    public void testOnDeleteUserEvent_WithMultipleTeams() {
        // Given
        Long userId = 1L;
        Long teamId1 = 10L;
        Long teamId2 = 20L;
        DeleteUserEvent event = new DeleteUserEvent(this, userId);

        TeamDTO team1 = new TeamDTO();
        team1.setId(teamId1);

        TeamDTO team2 = new TeamDTO();
        team2.setId(teamId2);

        List<TeamDTO> teams = Arrays.asList(team1, team2);

        when(teamService.findAllTeamsByUserId(userId)).thenReturn(teams);

        // When
        deleteUserEventListener.onDeleteUserEvent(event);

        // Then
        verify(teamService).findAllTeamsByUserId(userId);
        verify(teamService).removeUserFromTeam(userId, teamId1);
        verify(teamService).removeUserFromTeam(userId, teamId2);
    }
}
