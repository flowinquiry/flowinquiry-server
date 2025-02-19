package io.flowinquiry.modules.collab.controller;

import io.flowinquiry.modules.collab.domain.EntityType;
import io.flowinquiry.modules.collab.service.EntityWatcherService;
import io.flowinquiry.modules.fss.service.dto.EntityWatcherDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/entity-watchers")
public class EntityWatcherController {

    private final EntityWatcherService entityWatcherService;

    public EntityWatcherController(EntityWatcherService entityWatcherService) {
        this.entityWatcherService = entityWatcherService;
    }

    @DeleteMapping
    public void removeWatcher(
            @RequestParam String entityType,
            @RequestParam Long entityId,
            @RequestParam Long userId) {

        entityWatcherService.removeWatcher(EntityType.valueOf(entityType), entityId, userId);
    }

    @GetMapping
    public List<EntityWatcherDTO> getWatchersForEntity(
            @RequestParam String entityType, @RequestParam Long entityId) {

        return entityWatcherService.getWatchersForEntity(EntityType.valueOf(entityType), entityId);
    }

    @GetMapping("/user")
    public Page<EntityWatcherDTO> getWatchedEntitiesForUser(
            @RequestParam Long userId, Pageable pageable) {
        return entityWatcherService.getWatchedEntitiesForUser(userId, pageable);
    }

    @PostMapping("/add")
    public void addWatchersToEntity(
            @RequestParam String entityType,
            @RequestParam Long entityId,
            @RequestBody List<Long> watcherIds) {
        entityWatcherService.addWatchers(EntityType.valueOf(entityType), entityId, watcherIds);
    }

    @DeleteMapping("/remove")
    public void removeWatchersFromEntity(
            @RequestParam String entityType,
            @RequestParam Long entityId,
            @RequestBody List<Long> watcherIds) {
        entityWatcherService.removeWatchers(EntityType.valueOf(entityType), entityId, watcherIds);
    }
}
