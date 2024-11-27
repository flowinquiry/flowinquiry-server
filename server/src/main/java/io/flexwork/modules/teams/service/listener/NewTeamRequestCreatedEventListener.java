package io.flexwork.modules.teams.service.listener;

import static j2html.TagCreator.*;

import com.flexwork.platform.utils.Obfuscator;
import io.flexwork.modules.collab.domain.Notification;
import io.flexwork.modules.collab.repository.NotificationRepository;
import io.flexwork.modules.teams.repository.TeamRepository;
import io.flexwork.modules.teams.service.dto.TeamRequestDTO;
import io.flexwork.modules.teams.service.event.NewTeamRequestCreatedEvent;
import io.flexwork.modules.usermanagement.domain.User;
import io.flexwork.modules.usermanagement.service.dto.UserWithTeamRoleDTO;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class NewTeamRequestCreatedEventListener {
    private final NotificationRepository notificationRepository;
    private final TeamRepository teamRepository;

    public NewTeamRequestCreatedEventListener(
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
                                a("ticket ")
                                        .withHref(
                                                "/portal/teams/"
                                                        + Obfuscator.obfuscate(
                                                                teamRequestDTO.getTeamId())
                                                        + "/requests/"
                                                        + Obfuscator.obfuscate(
                                                                teamRequestDTO.getId())),
                                text(" has been created by "),
                                a(teamRequestDTO.getRequestUserName())
                                        .withHref(
                                                "/portals/users/"
                                                        + Obfuscator.obfuscate(
                                                                teamRequestDTO.getRequestUserId())))
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
                                                .createdAt(LocalDateTime.now())
                                                .build())
                        .toList();
        notificationRepository.saveAll(notifications);
    }
}
