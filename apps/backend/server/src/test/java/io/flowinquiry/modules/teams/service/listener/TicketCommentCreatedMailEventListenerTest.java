package io.flowinquiry.modules.teams.service.listener;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.flowinquiry.modules.collab.EmailContext;
import io.flowinquiry.modules.collab.domain.EntityType;
import io.flowinquiry.modules.collab.domain.EntityWatcher;
import io.flowinquiry.modules.collab.repository.EntityWatcherRepository;
import io.flowinquiry.modules.collab.service.CommentService;
import io.flowinquiry.modules.collab.service.MailService;
import io.flowinquiry.modules.collab.service.dto.CommentDTO;
import io.flowinquiry.modules.teams.service.TicketService;
import io.flowinquiry.modules.teams.service.dto.TicketDTO;
import io.flowinquiry.modules.teams.service.event.TicketCommentCreatedEvent;
import io.flowinquiry.modules.usermanagement.domain.User;
import io.flowinquiry.modules.usermanagement.service.dto.UserDTO;
import io.flowinquiry.modules.usermanagement.service.mapper.UserMapper;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

@ExtendWith(MockitoExtension.class)
public class TicketCommentCreatedMailEventListenerTest {

    @Mock private CommentService commentService;

    @Mock private UserMapper userMapper;

    @Mock private EntityWatcherRepository entityWatcherRepository;

    @Mock private TicketService ticketService;

    @Mock private MailService mailService;

    @Mock private MessageSource messageSource;

    private TicketCommentCreatedMailEventListener listener;

    @BeforeEach
    public void setup() {
        listener =
                new TicketCommentCreatedMailEventListener(
                        commentService,
                        userMapper,
                        entityWatcherRepository,
                        ticketService,
                        mailService,
                        messageSource);
    }

    @Test
    public void testOnTicketCommentCreated_NoWatchers() {
        // Given
        Long commentId = 1L;
        Long ticketId = 10L;
        Long userId = 100L;

        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(commentId);
        commentDTO.setEntityId(ticketId);
        commentDTO.setCreatedById(userId);
        commentDTO.setEntityType(EntityType.Ticket);

        TicketDTO ticketDTO = TicketDTO.builder().id(ticketId).requestTitle("Test Ticket").build();

        TicketCommentCreatedEvent event = new TicketCommentCreatedEvent(this, commentDTO);

        when(commentService.getCommentById(commentId)).thenReturn(commentDTO);
        when(entityWatcherRepository.findByEntityTypeAndEntityId(EntityType.Ticket, ticketId))
                .thenReturn(Collections.emptyList());
        when(ticketService.getTicketById(ticketId)).thenReturn(ticketDTO);

        // When
        listener.onTicketCommentCreated(event);

        // Then
        verify(commentService).getCommentById(commentId);
        verify(entityWatcherRepository).findByEntityTypeAndEntityId(EntityType.Ticket, ticketId);
        verify(ticketService).getTicketById(ticketId);
        verify(mailService, never()).sendEmail(any(EmailContext.class));
    }

    @Test
    public void testOnTicketCommentCreated_WithWatchers() {
        // Given
        Long commentId = 1L;
        Long ticketId = 10L;
        Long teamId = 20L;
        Long commenterId = 100L;
        Long watcher1Id = 101L;
        Long watcher2Id = 102L;

        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(commentId);
        commentDTO.setEntityId(ticketId);
        commentDTO.setCreatedById(commenterId);
        commentDTO.setEntityType(EntityType.Ticket);
        commentDTO.setContent("Test comment");

        TicketDTO ticketDTO =
                TicketDTO.builder().id(ticketId).teamId(teamId).requestTitle("Test Ticket").build();

        User watcher1User = User.builder().id(watcher1Id).build();
        User watcher2User = User.builder().id(watcher2Id).build();
        User commenterUser = User.builder().id(commenterId).build();

        EntityWatcher watcher1 =
                EntityWatcher.builder().id(watcher1Id).watchUser(watcher1User).build();

        EntityWatcher watcher2 =
                EntityWatcher.builder().id(watcher2Id).watchUser(watcher2User).build();

        EntityWatcher commenterWatcher =
                EntityWatcher.builder().id(commenterId).watchUser(commenterUser).build();

        List<EntityWatcher> watchers = Arrays.asList(watcher1, watcher2, commenterWatcher);

        UserDTO watcher1DTO = new UserDTO();
        watcher1DTO.setId(watcher1Id);
        watcher1DTO.setEmail("watcher1@example.com");

        UserDTO watcher2DTO = new UserDTO();
        watcher2DTO.setId(watcher2Id);
        watcher2DTO.setEmail("watcher2@example.com");

        TicketCommentCreatedEvent event = new TicketCommentCreatedEvent(this, commentDTO);

        when(commentService.getCommentById(commentId)).thenReturn(commentDTO);
        when(entityWatcherRepository.findByEntityTypeAndEntityId(EntityType.Ticket, ticketId))
                .thenReturn(watchers);
        when(ticketService.getTicketById(ticketId)).thenReturn(ticketDTO);
        when(userMapper.toDto(watcher1User)).thenReturn(watcher1DTO);
        when(userMapper.toDto(watcher2User)).thenReturn(watcher2DTO);
        when(mailService.getBaseUrl()).thenReturn("http://example.com");

        // When
        listener.onTicketCommentCreated(event);

        // Then
        verify(commentService).getCommentById(commentId);
        verify(entityWatcherRepository).findByEntityTypeAndEntityId(EntityType.Ticket, ticketId);
        verify(ticketService).getTicketById(ticketId);
        verify(userMapper).toDto(watcher1User);
        verify(userMapper).toDto(watcher2User);
        verify(userMapper, never()).toDto(commenterUser); // Commenter should not receive email
        verify(mailService, times(2)).sendEmail(any(EmailContext.class));
    }
}
