package io.flexwork.modules.collab.service;

import io.flexwork.modules.collab.domain.EntityType;
import io.flexwork.modules.collab.repository.ActivityLogRepository;
import io.flexwork.modules.collab.service.dto.ActivityLogDTO;
import io.flexwork.modules.collab.service.mapper.ActivityLogMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
            EntityType entityType,
            Long entityId,
            int page,
            int size,
            String sortBy,
            String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return activityLogRepository
                .findByEntityTypeAndEntityId(entityType, entityId, pageable)
                .map(activityLogMapper::toDTO);
    }
}
