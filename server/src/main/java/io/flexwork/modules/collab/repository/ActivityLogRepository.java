package io.flexwork.modules.collab.repository;

import io.flexwork.modules.collab.domain.ActivityLog;
import io.flexwork.modules.collab.domain.EntityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    Page<ActivityLog> findByEntityTypeAndEntityId(
            EntityType entityType, Long entityId, Pageable pageable);
}
