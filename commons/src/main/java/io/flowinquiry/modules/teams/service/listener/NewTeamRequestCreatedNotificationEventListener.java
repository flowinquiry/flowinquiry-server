package io.flowinquiry.modules.teams.service.listener;

import static j2html.TagCreator.a;
import static j2html.TagCreator.p;
import static j2html.TagCreator.text;

import io.flowinquiry.modules.collab.domain.ActivityLog;
import io.flowinquiry.modules.collab.domain.EntityType;
import io.flowinquiry.modules.collab.domain.Notification;
import io.flowinquiry.modules.collab.repository.ActivityLogRepository;
import io.flowinquiry.modules.collab.repository.NotificationRepository;
import io.flowinquiry.modules.teams.repository.TeamRepository;
import io.flowinquiry.modules.teams.service.dto.TeamRequestDTO;
import io.flowinquiry.modules.teams.service.event.NewTeamRequestCreatedEvent;
import io.flowinquiry.modules.usermanagement.domain.User;
import io.flowinquiry.modules.usermanagement.service.dto.UserWithTeamRoleDTO;
import io.flowinquiry.utils.Obfuscator;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class NewTeamRequestCreatedNotificationEventListener {
    private final NotificationRepository notificationRepository;
    private final TeamRepository teamRepository;
    private final ActivityLogRepository activityLogRepository;

    public NewTeamRequestCreatedNotificationEventListener(
            NotificationRepository notificationRepository,
            TeamRepository teamRepository,
            ActivityLogRepository activityLogRepository) {
        this.notificationRepository = notificationRepository;
        this.teamRepository = teamRepository;
        this.activityLogRepository = activityLogRepository;
    }

    @Async("asyncTaskExecutor")
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
