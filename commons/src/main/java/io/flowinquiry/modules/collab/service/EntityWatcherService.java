package io.flowinquiry.modules.collab.service;

import io.flowinquiry.modules.collab.domain.EntityType;
import io.flowinquiry.modules.collab.domain.EntityWatcher;
import io.flowinquiry.modules.collab.repository.EntityWatcherRepository;
import io.flowinquiry.modules.collab.service.mapper.EntityWatcherMapper;
import io.flowinquiry.modules.fss.service.dto.EntityWatcherDTO;
import io.flowinquiry.modules.usermanagement.domain.User;
import io.flowinquiry.modules.usermanagement.repository.UserRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EntityWatcherService {

    private final EntityWatcherRepository entityWatcherRepository;
    private final EntityWatcherMapper entityWatcherMapper;
    private final UserRepository userRepository;

    public EntityWatcherService(
            EntityWatcherRepository entityWatcherRepository,
            EntityWatcherMapper entityWatcherMapper,
            UserRepository userRepository) {
        this.entityWatcherRepository = entityWatcherRepository;
        this.entityWatcherMapper = entityWatcherMapper;
        this.userRepository = userRepository;
    }

    @Transactional
    public void addWatchers(EntityType entityType, Long entityId, List<Long> watcherIds) {
        // Fetch existing watchers for the given entity
        List<Long> existingWatcherIds =
                entityWatcherRepository.findWatcherIdsByEntity(entityType, entityId);

        // Filter out users who are already watchers
        List<Long> newWatcherIds =
                watcherIds.stream().filter(id -> !existingWatcherIds.contains(id)).toList();

        // Ensure we only fetch new users
        if (!newWatcherIds.isEmpty()) {
            List<User> newWatchers = userRepository.findAllById(newWatcherIds);

            List<EntityWatcher> entityWatchers =
                    newWatchers.stream()
                            .map(
                                    user ->
                                            EntityWatcher.builder()
                                                    .entityType(entityType)
                                                    .entityId(entityId)
                                                    .watchUser(user)
                                                    .build())
                            .toList();

            entityWatcherRepository.saveAll(entityWatchers);
        }
    }

    public void removeWatcher(EntityType entityType, Long entityId, Long userId) {
        entityWatcherRepository.deleteByEntityTypeAndEntityIdAndWatchUserId(
                entityType, entityId, userId);
    }

    @Transactional(readOnly = true)
    public List<EntityWatcherDTO> getWatchersForEntity(EntityType entityType, Long entityId) {
        return entityWatcherMapper.toDTOList(
                entityWatcherRepository.findByEntityTypeAndEntityId(entityType, entityId));
    }

    @Transactional(readOnly = true)
    public Page<EntityWatcherDTO> getWatchedEntitiesForUser(Long userId, Pageable pageable) {
        return entityWatcherRepository
                .findByWatchUserId(userId, pageable)
                .map(entityWatcherMapper::toDTO);
    }
}
