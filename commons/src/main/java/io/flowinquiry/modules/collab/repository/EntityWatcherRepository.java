package io.flowinquiry.modules.collab.repository;

import io.flowinquiry.modules.collab.domain.EntityType;
import io.flowinquiry.modules.collab.domain.EntityWatcher;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EntityWatcherRepository extends JpaRepository<EntityWatcher, Long> {

    @EntityGraph(attributePaths = {"watchUser"})
    List<EntityWatcher> findByEntityTypeAndEntityId(EntityType entityType, Long entityId);

    @Query(
            "SELECT ew.watchUser.id FROM EntityWatcher ew WHERE ew.entityType = :entityType AND ew.entityId = :entityId")
    List<Long> findWatcherIdsByEntity(
            @Param("entityType") EntityType entityType, @Param("entityId") Long entityId);

    boolean existsByEntityTypeAndEntityIdAndWatchUserId(
            EntityType entityType, Long entityId, Long watchUserId);

    Page<EntityWatcher> findByWatchUserId(Long userId, Pageable pageable);

    void deleteByEntityTypeAndEntityIdAndWatchUserId(
            EntityType entityType, Long entityId, Long watchUserId);
}
