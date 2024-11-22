package io.flexwork.modules.collab.service.listener;

import static j2html.TagCreator.*;

import io.flexwork.modules.collab.domain.Notification;
import io.flexwork.modules.collab.repository.NotificationRepository;
import io.flexwork.modules.collab.repository.TeamRepository;
import io.flexwork.modules.collab.service.event.NewTeamRequestCreatedEvent;
import io.flexwork.modules.teams.service.dto.TeamRequestDTO;
import io.flexwork.modules.usermanagement.domain.User;
import io.flexwork.modules.usermanagement.service.dto.UserWithTeamRoleDTO;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TeamRequestEventListener {
    private final NotificationRepository notificationRepository;
    private final TeamRepository teamRepository;

    public TeamRequestEventListener(
            NotificationRepository notificationRepository, TeamRepository teamRepository) {
        this.notificationRepository = notificationRepository;
        this.teamRepository = teamRepository;
    }

    @EventListener
    public void onNewTeamRequestCreated(NewTeamRequestCreatedEvent event) {
        TeamRequestDTO teamRequestDTO = event.getTeamRequest();
        String html =
                p(
                                text("A new "),
                                a("ticket request").withHref("#"),
                                text(" has been just created by "),
                                a("user").withHref("#"))
                        .render();

        List<UserWithTeamRoleDTO> usersInTeam =
                teamRepository.findUsersByTeamId(teamRequestDTO.getTeamId());
        List<Notification> notifications =
                usersInTeam.stream()
                        .map(
                                user ->
                                        Notification.builder()
                                                .content(html)
                                                .user(User.builder().id(user.getId()).build())
                                                .isRead(false)
                                                .createdAt(LocalDateTime.now())
                                                .build())
                        .toList();
        notificationRepository.saveAll(notifications);
    }
}
