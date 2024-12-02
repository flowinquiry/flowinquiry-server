package io.flexwork.modules.collab.repository;

import io.flexwork.modules.collab.domain.ActivityLog;
import io.flexwork.modules.collab.domain.EntityType;
import io.flexwork.modules.collab.service.dto.ActivityLogDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    Page<ActivityLog> findByEntityTypeAndEntityId(
            EntityType entityType, Long entityId, Pageable pageable);

    @Query(
            """
        SELECT new io.flexwork.modules.collab.service.dto.ActivityLogDTO(
            al.id, al.entityType, t.name, al.entityId, al.content, al.createdAt
        )
        FROM ActivityLog al
        JOIN Team t ON al.entityType = 'Team' AND al.entityId = t.id
        JOIN UserTeam ut ON ut.team.id = t.id
        WHERE ut.user.id = :userId
    """)
    Page<ActivityLogDTO> findAllByUserTeams(@Param("userId") Long userId, Pageable pageable);
}
