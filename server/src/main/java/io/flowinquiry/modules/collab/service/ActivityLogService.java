package io.flowinquiry.modules.collab.service;

import io.flowinquiry.modules.collab.domain.EntityType;
import io.flowinquiry.modules.collab.repository.ActivityLogRepository;
import io.flowinquiry.modules.collab.service.dto.ActivityLogDTO;
import io.flowinquiry.modules.collab.service.mapper.ActivityLogMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    private final ActivityLogMapper activityLogMapper;

    public ActivityLogService(
            ActivityLogRepository activityLogRepository, ActivityLogMapper activityLogMapper) {
        this.activityLogRepository = activityLogRepository;
        this.activityLogMapper = activityLogMapper;
    }

    public Page<ActivityLogDTO> getActivityLogs(
            EntityType entityType, Long entityId, Pageable pageable) {

        return activityLogRepository
                .findByEntityTypeAndEntityId(entityType, entityId, pageable)
                .map(activityLogMapper::toDTO);
    }

    public Page<ActivityLogDTO> getActivitiesForUser(Long userId, Pageable pageable) {
        return activityLogRepository.findAllByUserTeams(userId, pageable);
    }
}
