package io.flexwork.modules.teams.service.listener;

import static j2html.TagCreator.*;

import com.flexwork.platform.utils.Obfuscator;
import io.flexwork.modules.collab.domain.ActivityLog;
import io.flexwork.modules.collab.domain.EntityType;
import io.flexwork.modules.collab.repository.ActivityLogRepository;
import io.flexwork.modules.teams.domain.Team;
import io.flexwork.modules.teams.repository.TeamRepository;
import io.flexwork.modules.teams.service.event.NewUsersAddedIntoTeamEvent;
import io.flexwork.modules.usermanagement.domain.User;
import io.flexwork.modules.usermanagement.repository.UserRepository;
import io.flexwork.security.SecurityUtils;
import j2html.tags.specialized.DivTag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class NewUserAddedIntoTeamEventListener {

    private ActivityLogRepository activityLogRepository;
    private TeamRepository teamRepository;
    private UserRepository userRepository;

    public NewUserAddedIntoTeamEventListener(
            ActivityLogRepository activityLogRepository,
            TeamRepository teamRepository,
            UserRepository userRepository) {
        this.activityLogRepository = activityLogRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
    }

    @Async("auditLogExecutor")
    @EventListener
    @Transactional
    public void onNewUsersAddedIntoTeam(NewUsersAddedIntoTeamEvent event) {

        Team team =
                teamRepository
                        .findById(event.getTeamId())
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "Not found team id " + event.getTeamId()));
        List<User> allUsers = userRepository.findAllById(event.getUserIds());

        DivTag message = div();

        // Add message prefix
        message.withText("The following users have been added to the ")
                .with(b(team.getName()))
                .withText(" team as ")
                .with(b(event.getRoleName() + "s"))
                .withText(": ");

        // Construct user list
        message.with(
                ul().with(
                                allUsers.stream()
                                        .map(
                                                user ->
                                                        li().with(
                                                                        a(user.getFirstName()
                                                                                        + " "
                                                                                        + user
                                                                                                .getLastName())
                                                                                .withHref(
                                                                                        "/portal/users/"
                                                                                                + Obfuscator
                                                                                                        .obfuscate(
                                                                                                                user
                                                                                                                        .getId()))
                                                                                .withTarget(
                                                                                        "_blank")))
                                        .toList()));

        ActivityLog activityLog =
                ActivityLog.builder()
                        .entityId(team.getId())
                        .entityType(EntityType.Team)
                        .content(message.render())
                        .createdBy(SecurityUtils.getCurrentUserAuditorLogin())
                        .build();
        activityLogRepository.save(activityLog);
    }
}
