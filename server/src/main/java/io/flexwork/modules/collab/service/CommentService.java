package io.flexwork.modules.collab.service;

import io.flexwork.modules.collab.domain.Comment;
import io.flexwork.modules.collab.repository.CommentRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }

    public Comment getCommentById(Long id) {
        return commentRepository
                .findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException("Comment not found with id: " + id));
    }

    public List<Comment> getCommentsForEntity(String entityType, Long entityId) {
        return commentRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }

    public void deleteComment(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new IllegalArgumentException("Comment not found with id: " + id);
        }
        commentRepository.deleteById(id);
    }
}
