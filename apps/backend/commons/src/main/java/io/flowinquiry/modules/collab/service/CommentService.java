package io.flowinquiry.modules.collab.service;

import io.flowinquiry.modules.collab.domain.EntityType;
import io.flowinquiry.modules.collab.repository.CommentRepository;
import io.flowinquiry.modules.collab.service.dto.CommentDTO;
import io.flowinquiry.modules.collab.service.mapper.CommentMapper;
import io.flowinquiry.modules.teams.service.event.TicketCommentCreatedEvent;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CommentService {

    private final ApplicationEventPublisher eventPublisher;

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    public CommentService(
            ApplicationEventPublisher eventPublisher,
            CommentRepository commentRepository,
            CommentMapper commentMapper) {
        this.eventPublisher = eventPublisher;
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
    }

    public CommentDTO saveComment(CommentDTO comment) {
        CommentDTO savedComment =
                commentMapper.toDTO(commentRepository.save(commentMapper.toEntity(comment)));
        if (savedComment.getEntityType() == EntityType.Ticket) {
            eventPublisher.publishEvent(new TicketCommentCreatedEvent(this, savedComment));
        }
        return savedComment;
    }

    public CommentDTO getCommentById(Long id) {
        return commentRepository
                .findById(id)
                .map(commentMapper::toDTO)
                .orElseThrow(
                        () -> new IllegalArgumentException("Comment not found with id: " + id));
    }

    public List<CommentDTO> getCommentsForEntity(EntityType entityType, Long entityId) {
        return commentRepository
                .findByEntityTypeAndEntityIdOrderByCreatedAtDesc(entityType, entityId)
                .stream()
                .map(commentMapper::toDTO)
                .toList();
    }

    public void deleteComment(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new IllegalArgumentException("Comment not found with id: " + id);
        }
        commentRepository.deleteById(id);
    }
}
