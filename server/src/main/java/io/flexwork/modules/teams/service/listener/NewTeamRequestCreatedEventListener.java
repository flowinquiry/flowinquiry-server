package io.flexwork.modules.teams.service.listener;

import static j2html.TagCreator.*;

import com.flexwork.platform.utils.Obfuscator;
import io.flexwork.modules.collab.domain.ActivityLog;
import io.flexwork.modules.collab.domain.EntityType;
import io.flexwork.modules.collab.domain.Notification;
import io.flexwork.modules.collab.repository.ActivityLogRepository;
import io.flexwork.modules.collab.repository.NotificationRepository;
import io.flexwork.modules.teams.repository.TeamRepository;
import io.flexwork.modules.teams.service.dto.TeamRequestDTO;
import io.flexwork.modules.teams.service.event.NewTeamRequestCreatedEvent;
import io.flexwork.modules.usermanagement.domain.User;
import io.flexwork.modules.usermanagement.service.dto.UserWithTeamRoleDTO;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class NewTeamRequestCreatedEventListener {
    private final NotificationRepository notificationRepository;
    private final TeamRepository teamRepository;
    private final ActivityLogRepository activityLogRepository;

    public NewTeamRequestCreatedEventListener(
            NotificationRepository notificationRepository,
            TeamRepository teamRepository,
            ActivityLogRepository activityLogRepository) {
        this.notificationRepository = notificationRepository;
        this.teamRepository = teamRepository;
        this.activityLogRepository = activityLogRepository;
    }

    @Async("auditLogExecutor")
    @Transactional
    @EventListener
    public void onNewTeamRequestCreated(NewTeamRequestCreatedEvent event) {
        TeamRequestDTO teamRequestDTO = event.getTeamRequest();
        String html =
                p(
                                a(teamRequestDTO.getRequestUserName())
                                        .withHref(
                                                "/portal/users/"
                                                        + Obfuscator.obfuscate(
                                                                teamRequestDTO.getRequestUserId())),
                                text(" has created a new ticket "),
                                a(teamRequestDTO.getRequestTitle())
                                        .withHref(
                                                "/portal/teams/"
                                                        + Obfuscator.obfuscate(
                                                                teamRequestDTO.getTeamId())
                                                        + "/requests/"
                                                        + Obfuscator.obfuscate(
                                                                teamRequestDTO.getId())))
                        .render();

        List<UserWithTeamRoleDTO> usersInTeam =
                teamRepository.findUsersByTeamId(teamRequestDTO.getTeamId());
        List<Notification> notifications =
                usersInTeam.stream()
                        .filter(
                                user ->
                                        !user.getId()
                                                .equals(
                                                        teamRequestDTO
                                                                .getRequestUserId())) // Exclude
                        // creator
                        .map(
                                user ->
                                        Notification.builder()
                                                .content(html)
                                                .user(User.builder().id(user.getId()).build())
                                                .isRead(false)
                                                .build())
                        .toList();
        notificationRepository.saveAll(notifications);

        ActivityLog activityLog =
                ActivityLog.builder()
                        .entityId(teamRequestDTO.getTeamId())
                        .entityType(EntityType.Team)
                        .content(html)
                        .build();
        activityLogRepository.save(activityLog);
    }
}
