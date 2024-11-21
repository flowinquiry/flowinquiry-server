package io.flexwork.modules.collab.web.rest;

import io.flexwork.modules.collab.domain.Comment;
import io.flexwork.modules.collab.service.CommentService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<Comment> saveComment(@RequestBody Comment comment) {
        Comment savedComment = commentService.saveComment(comment);
        return ResponseEntity.ok(savedComment);
    }

    // Get a Comment by ID
    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Long id) {
        Comment comment = commentService.getCommentById(id);
        return ResponseEntity.ok(comment);
    }

    // Get Comments for an Entity
    @GetMapping
    public ResponseEntity<List<Comment>> getCommentsForEntity(
            @RequestParam String entityType, @RequestParam Long entityId) {
        List<Comment> comments = commentService.getCommentsForEntity(entityType, entityId);
        return ResponseEntity.ok(comments);
    }

    // Delete a Comment
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
