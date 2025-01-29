package io.flowinquiry.modules.teams.service.listener;

import static j2html.TagCreator.a;
import static j2html.TagCreator.p;
import static j2html.TagCreator.text;

import io.flowinquiry.exceptions.ResourceNotFoundException;
import io.flowinquiry.modules.collab.domain.ActivityLog;
import io.flowinquiry.modules.collab.domain.EntityType;
import io.flowinquiry.modules.collab.domain.Notification;
import io.flowinquiry.modules.collab.repository.ActivityLogRepository;
import io.flowinquiry.modules.collab.repository.NotificationRepository;
import io.flowinquiry.modules.collab.service.dto.CommentDTO;
import io.flowinquiry.modules.teams.domain.TeamRequest;
import io.flowinquiry.modules.teams.repository.TeamRepository;
import io.flowinquiry.modules.teams.repository.TeamRequestRepository;
import io.flowinquiry.modules.teams.service.event.TeamRequestCommentCreatedEvent;
import io.flowinquiry.modules.usermanagement.domain.User;
import io.flowinquiry.modules.usermanagement.repository.UserRepository;
import io.flowinquiry.modules.usermanagement.service.dto.UserWithTeamRoleDTO;
import io.flowinquiry.utils.Obfuscator;
import io.flowinquiry.utils.StringUtils;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TeamRequestCommentCreatedNotificationEventListener {

    private final UserRepository userRepository;
    private final TeamRequestRepository teamRequestRepository;
    private final TeamRepository teamRepository;
    private final NotificationRepository notificationRepository;
    private final ActivityLogRepository activityLogRepository;

    public TeamRequestCommentCreatedNotificationEventListener(
            UserRepository userRepository,
            TeamRequestRepository teamRequestRepository,
            TeamRepository teamRepository,
            NotificationRepository notificationRepository,
            ActivityLogRepository activityLogRepository) {
        this.userRepository = userRepository;
        this.teamRequestRepository = teamRequestRepository;
        this.teamRepository = teamRepository;
        this.notificationRepository = notificationRepository;
        this.activityLogRepository = activityLogRepository;
    }

    @Async("asyncTaskExecutor")
    @Transactional
    @EventListener
    public void onTeamRequestCommentCreated(TeamRequestCommentCreatedEvent event) {
        CommentDTO commentDTO = event.getCommentDTO();

        User createdUser =
                userRepository
                        .findById(commentDTO.getCreatedById())
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "User not found " + commentDTO.getCreatedById()));
        TeamRequest teamRequest =
                teamRequestRepository
                        .findById(commentDTO.getEntityId())
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "TeamRequest not found "
                                                        + commentDTO.getEntityId()));

        String commentContent = StringUtils.polishedHtmlTagsMessage(commentDTO.getContent());
        String truncatedContent =
                commentContent.length() > 50
                        ? commentContent.substring(0, 50) + "..."
                        : commentContent;

        String html =
                p(
                                a(createdUser.getFirstName() + " " + createdUser.getLastName())
                                        .withHref(
                                                "/portal/users/"
                                                        + Obfuscator.obfuscate(
                                                                createdUser.getId())),
                                text(" has created a new comment for the request "),
                                a(teamRequest.getRequestTitle())
                                        .withHref(
                                                "/portal/teams/"
                                                        + Obfuscator.obfuscate(
                                                                teamRequest.getTeam().getId())
                                                        + "/requests/"
                                                        + Obfuscator.obfuscate(
                                                                teamRequest.getId())),
                                text(": " + truncatedContent))
                        .render();

        List<UserWithTeamRoleDTO> usersInTeam =
                teamRepository.findUsersByTeamId(teamRequest.getTeam().getId());
        List<Notification> notifications =
                usersInTeam.stream()
                        .filter(user -> !user.getId().equals(commentDTO.getCreatedById()))
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
                        .entityId(teamRequest.getTeam().getId())
                        .entityType(EntityType.Team)
                        .content(html)
                        .build();
        activityLogRepository.save(activityLog);
    }
}
