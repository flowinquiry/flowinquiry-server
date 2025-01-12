package io.flowinquiry.modules.fss.repository;

import io.flowinquiry.modules.fss.domain.EntityAttachment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntityAttachmentRepository extends JpaRepository<EntityAttachment, Long> {

    /**
     * Finds all attachments for a specific entity type and entity ID.
     *
     * @param entityType The type of entity (e.g., "team_request", "comment").
     * @param entityId The ID of the entity.
     * @return A list of attachments for the specified entity.
     */
    List<EntityAttachment> findByEntityTypeAndEntityId(String entityType, Long entityId);

    /**
     * Deletes all attachments for a specific entity type and entity ID.
     *
     * @param entityType The type of entity (e.g., "team_request", "comment").
     * @param entityId The ID of the entity.
     */
    void deleteByEntityTypeAndEntityId(String entityType, Long entityId);
}
