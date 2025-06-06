package io.flowinquiry.modules.teams.service.listener;

import static io.flowinquiry.modules.teams.utils.TicketPathUtils.buildTicketPath;
import static j2html.TagCreator.a;
import static j2html.TagCreator.p;
import static j2html.TagCreator.text;

import io.flowinquiry.exceptions.ResourceNotFoundException;
import io.flowinquiry.modules.collab.domain.ActivityLog;
import io.flowinquiry.modules.collab.domain.EntityType;
import io.flowinquiry.modules.collab.domain.Notification;
import io.flowinquiry.modules.collab.domain.NotificationType;
import io.flowinquiry.modules.collab.repository.ActivityLogRepository;
import io.flowinquiry.modules.collab.repository.NotificationRepository;
import io.flowinquiry.modules.teams.repository.TeamRepository;
import io.flowinquiry.modules.teams.service.TicketService;
import io.flowinquiry.modules.teams.service.dto.TicketDTO;
import io.flowinquiry.modules.teams.service.event.NewTicketCreatedEvent;
import io.flowinquiry.modules.usermanagement.domain.User;
import io.flowinquiry.modules.usermanagement.repository.UserRepository;
import io.flowinquiry.modules.usermanagement.service.dto.UserWithTeamRoleDTO;
import io.flowinquiry.utils.Obfuscator;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class NewTicketCreatedNotificationEventListener {
    private final TicketService ticketService;
    private final SimpMessagingTemplate messageTemplate;
    private final NotificationRepository notificationRepository;
    private final TeamRepository teamRepository;
    private final ActivityLogRepository activityLogRepository;
    private final UserRepository userRepository;

    public NewTicketCreatedNotificationEventListener(
            TicketService ticketService,
            SimpMessagingTemplate messageTemplate,
            NotificationRepository notificationRepository,
            TeamRepository teamRepository,
            ActivityLogRepository activityLogRepository,
            UserRepository userRepository) {
        this.ticketService = ticketService;
        this.messageTemplate = messageTemplate;
        this.notificationRepository = notificationRepository;
        this.teamRepository = teamRepository;
        this.activityLogRepository = activityLogRepository;
        this.userRepository = userRepository;
    }

    @Async("asyncTaskExecutor")
    @Transactional
    @EventListener
    public void onNewTicketCreated(NewTicketCreatedEvent event) {
        TicketDTO ticketDTO = ticketService.getTicketById(event.getTicket().getId());
        User requestUser =
                userRepository
                        .findOneById(ticketDTO.getRequestUserId())
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "User not found: " + ticketDTO.getRequestUserId()));
        String html =
                p(
                                a(requestUser.getFirstName() + " " + requestUser.getLastName())
                                        .withHref(
                                                "/portal/users/"
                                                        + Obfuscator.obfuscate(
                                                                ticketDTO.getRequestUserId())),
                                text(" has created a new ticket "),
                                a(ticketDTO.getRequestTitle()).withHref(buildTicketPath(ticketDTO)))
                        .render();

        List<UserWithTeamRoleDTO> usersInTeam =
                teamRepository.findUsersByTeamId(ticketDTO.getTeamId());

        List<Notification> notifications = new ArrayList<>();

        for (UserWithTeamRoleDTO user : usersInTeam) {
            if (!user.getId().equals(ticketDTO.getRequestUserId())) {
                Notification notification =
                        Notification.builder()
                                .content(html)
                                .type(NotificationType.INFO)
                                .user(User.builder().id(user.getId()).build())
                                .isRead(false)
                                .build();

                notifications.add(notification);
            }
        }

        List<Notification> savedNotifications = notificationRepository.saveAll(notifications);

        for (Notification notification : savedNotifications) {
            messageTemplate.convertAndSendToUser(
                    String.valueOf(notification.getUser().getId()),
                    "/queue/notifications",
                    notification);
        }

        ActivityLog activityLog =
                ActivityLog.builder()
                        .entityId(ticketDTO.getTeamId())
                        .entityType(EntityType.Team)
                        .content(html)
                        .build();
        activityLogRepository.save(activityLog);
    }
}
