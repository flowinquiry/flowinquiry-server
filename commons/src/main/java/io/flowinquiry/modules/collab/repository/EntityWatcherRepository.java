package io.flowinquiry.modules.collab.repository;

import io.flowinquiry.modules.collab.domain.EntityType;
import io.flowinquiry.modules.collab.domain.EntityWatcher;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntityWatcherRepository extends JpaRepository<EntityWatcher, Long> {

    @EntityGraph(attributePaths = {"watchUser"})
    List<EntityWatcher> findByEntityTypeAndEntityId(EntityType entityType, Long entityId);

    Page<EntityWatcher> findByWatchUserId(Long userId, Pageable pageable);

    boolean existsByEntityTypeAndEntityIdAndWatchUserId(
            EntityType entityType, Long entityId, Long watchUserId);

    void deleteByEntityTypeAndEntityIdAndWatchUserId(
            EntityType entityType, Long entityId, Long watchUserId);
}
