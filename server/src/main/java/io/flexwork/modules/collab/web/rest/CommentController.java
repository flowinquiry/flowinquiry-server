package io.flexwork.modules.collab.web.rest;

import io.flexwork.modules.collab.domain.Comment;
import io.flexwork.modules.collab.service.CommentService;
import io.flexwork.modules.collab.service.dto.CommentDTO;
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
    public ResponseEntity<CommentDTO> saveComment(@RequestBody CommentDTO comment) {
        CommentDTO savedComment = commentService.saveComment(comment);
        return ResponseEntity.ok(savedComment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Long id) {
        Comment comment = commentService.getCommentById(id);
        return ResponseEntity.ok(comment);
    }

    @GetMapping
    public ResponseEntity<List<CommentDTO>> getCommentsForEntity(
            @RequestParam String entityType, @RequestParam Long entityId) {
        List<CommentDTO> comments = commentService.getCommentsForEntity(entityType, entityId);
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
