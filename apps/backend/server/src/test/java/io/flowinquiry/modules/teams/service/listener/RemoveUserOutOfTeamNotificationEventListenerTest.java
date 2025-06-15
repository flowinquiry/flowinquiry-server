package io.flowinquiry.modules.teams.service.listener;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.flowinquiry.modules.collab.domain.ActivityLog;
import io.flowinquiry.modules.collab.domain.EntityType;
import io.flowinquiry.modules.collab.repository.ActivityLogRepository;
import io.flowinquiry.modules.teams.domain.Team;
import io.flowinquiry.modules.teams.repository.TeamRepository;
import io.flowinquiry.modules.teams.service.event.RemoveUserOutOfTeamEvent;
import io.flowinquiry.modules.usermanagement.domain.User;
import io.flowinquiry.modules.usermanagement.repository.UserRepository;
import io.flowinquiry.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RemoveUserOutOfTeamNotificationEventListenerTest {

    @Mock private TeamRepository teamRepository;

    @Mock private UserRepository userRepository;

    @Mock private ActivityLogRepository activityLogRepository;

    private RemoveUserOutOfTeamNotificationEventListener listener;

    @BeforeEach
    public void setup() {
        listener =
                new RemoveUserOutOfTeamNotificationEventListener(
                        teamRepository, userRepository, activityLogRepository);
    }

    @Test
    public void testOnRemoveUserOutOfTeam_Success() {
        // Given
        Long teamId = 1L;
        Long userId = 101L;

        RemoveUserOutOfTeamEvent event = new RemoveUserOutOfTeamEvent(this, teamId, userId);

        Team team = Team.builder().id(teamId).name("Test Team").build();

        User user = User.builder().id(userId).firstName("John").lastName("Doe").build();

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            securityUtils
                    .when(SecurityUtils::getCurrentUserAuditorLogin)
                    .thenReturn(User.builder().id(999L).build());

            // When
            listener.onRemoveUserOutOfTeam(event);

            // Then
            verify(teamRepository).findById(teamId);
            verify(userRepository).findById(userId);

            ArgumentCaptor<ActivityLog> activityLogCaptor =
                    ArgumentCaptor.forClass(ActivityLog.class);
            verify(activityLogRepository).save(activityLogCaptor.capture());

            ActivityLog capturedActivityLog = activityLogCaptor.getValue();
            assert capturedActivityLog.getEntityId().equals(teamId);
            assert capturedActivityLog.getEntityType().equals(EntityType.Team);
            assert capturedActivityLog.getCreatedBy().getId().equals(999L);
            assert capturedActivityLog.getContent().contains("John Doe");
            assert capturedActivityLog.getContent().contains("Test Team");
            assert capturedActivityLog.getContent().contains("no longer part of");
        }
    }

    @Test
    public void testOnRemoveUserOutOfTeam_TeamNotFound() {
        // Given
        Long teamId = 1L;
        Long userId = 101L;

        RemoveUserOutOfTeamEvent event = new RemoveUserOutOfTeamEvent(this, teamId, userId);

        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        // When & Then
        try {
            listener.onRemoveUserOutOfTeam(event);
        } catch (EntityNotFoundException e) {
            assert e.getMessage().contains("Not found team id " + teamId);
        }

        verify(teamRepository).findById(teamId);
    }

    @Test
    public void testOnRemoveUserOutOfTeam_UserNotFound() {
        // Given
        Long teamId = 1L;
        Long userId = 101L;

        RemoveUserOutOfTeamEvent event = new RemoveUserOutOfTeamEvent(this, teamId, userId);

        Team team = Team.builder().id(teamId).name("Test Team").build();

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        try {
            listener.onRemoveUserOutOfTeam(event);
        } catch (EntityNotFoundException e) {
            assert e.getMessage().contains("Not found user id " + userId);
        }

        verify(teamRepository).findById(teamId);
        verify(userRepository).findById(userId);
    }
}
