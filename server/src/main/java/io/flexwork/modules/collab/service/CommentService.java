package io.flexwork.modules.collab.service;

import io.flexwork.modules.collab.domain.Comment;
import io.flexwork.modules.collab.repository.CommentRepository;
import io.flexwork.modules.collab.service.dto.CommentDTO;
import io.flexwork.modules.collab.service.mapper.CommentMapper;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    public CommentService(CommentRepository commentRepository, CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
    }

    public CommentDTO saveComment(CommentDTO comment) {
        return commentMapper.toDTO(commentRepository.save(commentMapper.toEntity(comment)));
    }

    public Comment getCommentById(Long id) {
        return commentRepository
                .findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException("Comment not found with id: " + id));
    }

    public List<CommentDTO> getCommentsForEntity(String entityType, Long entityId) {
        return commentRepository.findByEntityTypeAndEntityId(entityType, entityId).stream()
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
