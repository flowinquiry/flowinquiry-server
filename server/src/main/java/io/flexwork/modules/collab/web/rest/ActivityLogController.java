package io.flexwork.modules.collab.web.rest;

import io.flexwork.modules.collab.domain.EntityType;
import io.flexwork.modules.collab.service.ActivityLogService;
import io.flexwork.modules.collab.service.dto.ActivityLogDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
            @RequestParam EntityType entityType,
            @RequestParam Long entityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        Page<ActivityLogDTO> activityLogs =
                activityLogService.getActivityLogs(
                        entityType, entityId, page, size, sortBy, sortDirection);
        return ResponseEntity.ok(activityLogs);
    }
}
