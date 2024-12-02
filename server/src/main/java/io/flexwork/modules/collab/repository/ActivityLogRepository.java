package io.flexwork.modules.collab.repository;

import io.flexwork.modules.collab.domain.ActivityLog;
import io.flexwork.modules.collab.domain.EntityType;
import io.flexwork.modules.collab.service.dto.ActivityLogDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    @EntityGraph(attributePaths = {"createdBy"})
    Page<ActivityLog> findByEntityTypeAndEntityId(
            EntityType entityType, Long entityId, Pageable pageable);

    @Query(
            """
            SELECT new io.flexwork.modules.collab.service.dto.ActivityLogDTO(
                al.id, al.entityType, t.name, al.entityId, al.content, al.createdAt, u.id,
                CONCAT(COALESCE(u.firstName, ''), ' ', COALESCE(u.lastName, '')),
                u.imageUrl
            )
            FROM ActivityLog al
            JOIN Team t ON al.entityType = 'Team' AND al.entityId = t.id
            JOIN t.users u
            WHERE u.id = :userId
            """)
    Page<ActivityLogDTO> findAllByUserTeams(@Param("userId") Long userId, Pageable pageable);
}
