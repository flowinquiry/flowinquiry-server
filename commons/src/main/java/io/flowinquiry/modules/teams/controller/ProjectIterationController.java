package io.flowinquiry.modules.teams.controller;

import io.flowinquiry.exceptions.ResourceNotFoundException;
import io.flowinquiry.modules.teams.service.ProjectIterationService;
import io.flowinquiry.modules.teams.service.dto.ProjectIterationDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/project-iterations")
@RequiredArgsConstructor
public class ProjectIterationController {

    private final ProjectIterationService iterationService;

    @GetMapping("/{projectId}")
    public List<ProjectIterationDTO> getAllIterations(@PathVariable Long projectId) {
        return iterationService.getAllIterations(projectId);
    }

    @GetMapping("/{id}")
    public ProjectIterationDTO getIterationById(@PathVariable Long id) {
        return iterationService
                .getIterationById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Can not find iteration id " + id));
    }

    @PostMapping
    public ProjectIterationDTO createIteration(@RequestBody ProjectIterationDTO iteration) {
        return iterationService.save(iteration);
    }

    @PutMapping("/{id}")
    public ProjectIterationDTO updateIteration(
            @PathVariable Long id, @RequestBody ProjectIterationDTO updatedIteration) {
        return iterationService.updateIteration(id, updatedIteration);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIteration(@PathVariable Long id) {
        iterationService.deleteIteration(id);
        return ResponseEntity.noContent().build();
    }
}
