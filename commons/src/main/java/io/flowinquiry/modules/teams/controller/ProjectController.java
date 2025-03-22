package io.flowinquiry.modules.teams.controller;

import io.flowinquiry.exceptions.ResourceNotFoundException;
import io.flowinquiry.modules.teams.service.ProjectEpicService;
import io.flowinquiry.modules.teams.service.ProjectIterationService;
import io.flowinquiry.modules.teams.service.ProjectService;
import io.flowinquiry.modules.teams.service.dto.ProjectDTO;
import io.flowinquiry.modules.teams.service.dto.ProjectEpicDTO;
import io.flowinquiry.modules.teams.service.dto.ProjectIterationDTO;
import io.flowinquiry.query.QueryDTO;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
@AllArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    private final ProjectIterationService projectIterationService;

    private final ProjectEpicService projectEpicService;

    @PostMapping
    public ProjectDTO createProject(@RequestBody ProjectDTO project) {
        return projectService.createProject(project);
    }

    @GetMapping("/{id}")
    public ProjectDTO getProjectById(@PathVariable Long id) {
        return projectService
                .getProjectById(id)
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "Project  with id " + id + " not found"));
    }

    @PostMapping("/search")
    public Page<ProjectDTO> findProject(
            @Valid @RequestBody Optional<QueryDTO> queryDTO, Pageable pageable) {
        return projectService.findProjects(queryDTO, pageable);
    }

    @PutMapping("/{projectId}")
    public ProjectDTO updateProject(
            @PathVariable("projectId") Long projectId, @RequestBody ProjectDTO project) {
        return projectService.updateProject(projectId, project);
    }

    @DeleteMapping("/{projectId}")
    public void deleteProject(@PathVariable("projectId") Long projectId) {
        projectService.deleteProject(projectId);
    }

    @GetMapping("/{projectId}/iterations")
    public List<ProjectIterationDTO> getProjectIterations(@PathVariable Long projectId) {
        return projectIterationService.findByProjectId(projectId);
    }

    @GetMapping("/{projectId}/epics")
    public List<ProjectEpicDTO> getProjectEpics(@PathVariable Long projectId) {
        return projectEpicService.findByProjectId(projectId);
    }
}
