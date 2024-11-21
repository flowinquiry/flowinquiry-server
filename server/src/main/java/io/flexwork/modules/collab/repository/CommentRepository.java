package io.flexwork.modules.collab.repository;

import io.flexwork.modules.collab.domain.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByEntityTypeAndEntityId(String entityType, Long entityId);
}
