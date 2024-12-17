package io.flowinquiry.modules.collab.repository;

import io.flowinquiry.modules.collab.domain.Comment;
import io.flowinquiry.modules.collab.domain.EntityType;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(attributePaths = "createdBy")
    List<Comment> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(
            EntityType entityType, Long entityId);
}
