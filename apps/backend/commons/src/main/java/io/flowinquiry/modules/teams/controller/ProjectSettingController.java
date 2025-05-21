package io.flowinquiry.modules.teams.controller;

import io.flowinquiry.modules.teams.service.ProjectSettingService;
import io.flowinquiry.modules.teams.service.dto.ProjectSettingDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/project-settings")
@RequiredArgsConstructor
public class ProjectSettingController {

    private final ProjectSettingService projectSettingService;

    @GetMapping("/project/{projectId}")
    public ProjectSettingDTO getByProjectId(@PathVariable Long projectId) {
        return projectSettingService.getByProjectId(projectId);
    }

    @PostMapping
    public ProjectSettingDTO create(@RequestBody ProjectSettingDTO dto) {
        return projectSettingService.save(dto);
    }

    @PutMapping("/{id}")
    public ProjectSettingDTO update(@PathVariable Long id, @RequestBody ProjectSettingDTO dto) {
        return projectSettingService.update(id, dto);
    }
}
