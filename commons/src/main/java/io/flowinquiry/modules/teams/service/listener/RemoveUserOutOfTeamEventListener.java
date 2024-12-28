package io.flowinquiry.modules.teams.service.listener;

import static j2html.TagCreator.a;
import static j2html.TagCreator.div;

import io.flowinquiry.modules.collab.domain.ActivityLog;
import io.flowinquiry.modules.collab.domain.EntityType;
import io.flowinquiry.modules.collab.repository.ActivityLogRepository;
import io.flowinquiry.modules.teams.domain.Team;
import io.flowinquiry.modules.teams.repository.TeamRepository;
import io.flowinquiry.modules.teams.service.event.RemoveUserOutOfTeamEvent;
import io.flowinquiry.modules.usermanagement.domain.User;
import io.flowinquiry.modules.usermanagement.repository.UserRepository;
import io.flowinquiry.security.SecurityUtils;
import io.flowinquiry.utils.Obfuscator;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RemoveUserOutOfTeamEventListener {

    private final TeamRepository teamRepository;

    private final UserRepository userRepository;

    private final ActivityLogRepository activityLogRepository;

    public RemoveUserOutOfTeamEventListener(
            TeamRepository teamRepository,
            UserRepository userRepository,
            ActivityLogRepository activityLogRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.activityLogRepository = activityLogRepository;
    }

    @Async("auditLogExecutor")
    @EventListener
    @Transactional
    public void onRemoveUserOutOfTeam(RemoveUserOutOfTeamEvent event) {
        Team team =
                teamRepository
                        .findById(event.getTeamId())
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "Not found team id " + event.getTeamId()));

        User user =
                userRepository
                        .findById(event.getUserId())
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "Not found user id " + event.getUserId()));

        String content =
                div().withText("User ")
                        .with(
                                a(user.getFirstName() + " " + user.getLastName())
                                        .withHref(
                                                "/portal/users/"
                                                        + Obfuscator.obfuscate(user.getId()))
                                        .withTarget("_blank"))
                        .withText(" is no longer part of the ")
                        .with(
                                a(team.getName())
                                        .withHref(
                                                "/portal/teams/"
                                                        + Obfuscator.obfuscate(team.getId()))
                                        .withTarget("_blank") // Opens the link in a new tab
                                )
                        .withText(" team.")
                        .render();

        ActivityLog activityLog =
                ActivityLog.builder()
                        .entityId(team.getId())
                        .entityType(EntityType.Team)
                        .content(content)
                        .createdBy(SecurityUtils.getCurrentUserAuditorLogin())
                        .build();
        activityLogRepository.save(activityLog);
    }
}
