package io.flexwork.modules.teams.service.listener;

import io.flexwork.modules.collab.domain.ActivityLog;
import io.flexwork.modules.collab.domain.EntityType;
import io.flexwork.modules.collab.repository.ActivityLogRepository;
import io.flexwork.modules.teams.domain.Team;
import io.flexwork.modules.teams.repository.TeamRepository;
import io.flexwork.modules.teams.service.event.NewUsersAddedIntoTeamEvent;
import io.flexwork.modules.usermanagement.domain.User;
import io.flexwork.modules.usermanagement.repository.UserRepository;
import io.flexwork.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.context.event.EventListener;

public class NewUserAddedIntoTeamEventListener {

    private ActivityLogRepository activityLogRepository;
    private TeamRepository teamRepository;
    private UserRepository userRepository;

    public NewUserAddedIntoTeamEventListener(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    @EventListener
    @Transactional
    public void onNewUsersAddedIntoTeam(NewUsersAddedIntoTeamEvent event) {
        ActivityLog activityLog = new ActivityLog();
        activityLog.setEntityType(EntityType.Team);
        activityLog.setEntityId(event.getTeamId());
        Team team =
                teamRepository
                        .findById(event.getTeamId())
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "Not found team id " + event.getTeamId()));
        List<User> allUsers = userRepository.findAllById(event.getUserIds());

        activityLog.setCreatedBy(
                SecurityUtils.getCurrentUserLogin()
                        .map(userKey -> User.builder().id(userKey.getId()).build())
                        .orElse(null));
        activityLogRepository.save(activityLog);
    }
}
