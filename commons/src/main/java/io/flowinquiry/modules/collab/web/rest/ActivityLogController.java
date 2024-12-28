package io.flowinquiry.modules.collab.web.rest;

import io.flowinquiry.modules.collab.domain.EntityType;
import io.flowinquiry.modules.collab.service.ActivityLogService;
import io.flowinquiry.modules.collab.service.dto.ActivityLogDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/activity-logs")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    @GetMapping
    public ResponseEntity<Page<ActivityLogDTO>> getActivityLogs(
            @RequestParam("entityType") EntityType entityType, @RequestParam("entityId") Long entityId, Pageable pageable) {
        Page<ActivityLogDTO> activityLogs =
                activityLogService.getActivityLogs(entityType, entityId, pageable);
        return ResponseEntity.ok(activityLogs);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ActivityLogDTO>> getUserActivities(
            @PathVariable("userId") Long userId, Pageable pageable) {
        Page<ActivityLogDTO> activities = activityLogService.getActivitiesForUser(userId, pageable);
        return ResponseEntity.ok(activities);
    }
}
