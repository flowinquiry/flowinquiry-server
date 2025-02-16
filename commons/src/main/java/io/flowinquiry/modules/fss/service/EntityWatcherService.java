package io.flowinquiry.modules.fss.service;

import io.flowinquiry.exceptions.ResourceNotFoundException;
import io.flowinquiry.modules.collab.domain.EntityType;
import io.flowinquiry.modules.collab.domain.EntityWatcher;
import io.flowinquiry.modules.collab.repository.EntityWatcherRepository;
import io.flowinquiry.modules.fss.service.dto.EntityWatcherDTO;
import io.flowinquiry.modules.fss.service.mapper.EntityWatcherMapper;
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

    public EntityWatcherDTO addWatcher(EntityType entityType, Long entityId, Long userId) {
        if (entityWatcherRepository.existsByEntityTypeAndEntityIdAndWatchUserId(
                entityType, entityId, userId)) {
            throw new IllegalStateException("User is already watching this entity.");
        }

        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        EntityWatcher watcher = new EntityWatcher();
        watcher.setEntityType(entityType);
        watcher.setEntityId(entityId);
        watcher.setWatchUser(user);

        return entityWatcherMapper.toDTO(entityWatcherRepository.save(watcher));
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
