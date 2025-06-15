package io.flowinquiry.modules.teams.service.listener;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.flowinquiry.modules.collab.domain.ActivityLog;
import io.flowinquiry.modules.collab.domain.EntityType;
import io.flowinquiry.modules.collab.repository.ActivityLogRepository;
import io.flowinquiry.modules.teams.domain.Team;
import io.flowinquiry.modules.teams.repository.TeamRepository;
import io.flowinquiry.modules.teams.service.event.NewUsersAddedIntoTeamEvent;
import io.flowinquiry.modules.usermanagement.domain.User;
import io.flowinquiry.modules.usermanagement.repository.UserRepository;
import io.flowinquiry.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
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
public class NewUserAddedIntoTeamNotificationEventListenerTest {

    @Mock private ActivityLogRepository activityLogRepository;

    @Mock private TeamRepository teamRepository;

    @Mock private UserRepository userRepository;

    private NewUserAddedIntoTeamNotificationEventListener listener;

    @BeforeEach
    public void setup() {
        listener =
                new NewUserAddedIntoTeamNotificationEventListener(
                        activityLogRepository, teamRepository, userRepository);
    }

    @Test
    public void testOnNewUsersAddedIntoTeam_Success() {
        // Given
        Long teamId = 1L;
        Long userId1 = 101L;
        Long userId2 = 102L;
        List<Long> userIds = Arrays.asList(userId1, userId2);
        String roleName = "Developer";

        NewUsersAddedIntoTeamEvent event =
                new NewUsersAddedIntoTeamEvent(this, userIds, teamId, roleName);

        Team team = Team.builder().id(teamId).name("Test Team").build();

        User user1 = User.builder().id(userId1).firstName("John").lastName("Doe").build();

        User user2 = User.builder().id(userId2).firstName("Jane").lastName("Smith").build();

        List<User> users = Arrays.asList(user1, user2);

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(userRepository.findAllById(userIds)).thenReturn(users);

        try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            securityUtils
                    .when(SecurityUtils::getCurrentUserAuditorLogin)
                    .thenReturn(User.builder().id(999L).build());

            // When
            listener.onNewUsersAddedIntoTeam(event);

            // Then
            verify(teamRepository).findById(teamId);
            verify(userRepository).findAllById(userIds);

            ArgumentCaptor<ActivityLog> activityLogCaptor =
                    ArgumentCaptor.forClass(ActivityLog.class);
            verify(activityLogRepository).save(activityLogCaptor.capture());

            ActivityLog capturedActivityLog = activityLogCaptor.getValue();
            assert capturedActivityLog.getEntityId().equals(teamId);
            assert capturedActivityLog.getEntityType().equals(EntityType.Team);
            assert capturedActivityLog.getCreatedBy().getId().equals(999L);
            assert capturedActivityLog.getContent().contains("Test Team");
            assert capturedActivityLog.getContent().contains("Developers");
            assert capturedActivityLog.getContent().contains("John Doe");
            assert capturedActivityLog.getContent().contains("Jane Smith");
        }
    }

    @Test
    public void testOnNewUsersAddedIntoTeam_TeamNotFound() {
        // Given
        Long teamId = 1L;
        Long userId1 = 101L;
        Long userId2 = 102L;
        List<Long> userIds = Arrays.asList(userId1, userId2);
        String roleName = "Developer";

        NewUsersAddedIntoTeamEvent event =
                new NewUsersAddedIntoTeamEvent(this, userIds, teamId, roleName);

        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        // When & Then
        try {
            listener.onNewUsersAddedIntoTeam(event);
        } catch (EntityNotFoundException e) {
            assert e.getMessage().contains("Not found team id " + teamId);
        }

        verify(teamRepository).findById(teamId);
    }
}
