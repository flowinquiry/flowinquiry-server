package io.flowinquiry.modules.teams.service;

import static java.util.stream.Collectors.toList;

import io.flowinquiry.modules.teams.domain.Project;
import io.flowinquiry.modules.teams.domain.ProjectEpic;
import io.flowinquiry.modules.teams.repository.ProjectEpicRepository;
import io.flowinquiry.modules.teams.repository.ProjectRepository;
import io.flowinquiry.modules.teams.service.dto.ProjectEpicDTO;
import io.flowinquiry.modules.teams.service.mapper.ProjectEpicMapper;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectEpicService {

    private final ProjectEpicMapper projectEpicMapper;

    private final ProjectEpicRepository projectEpicRepository;

    private final ProjectRepository projectRepository;

    public List<ProjectEpicDTO> getAllEpics(Long projectId) {
        return projectEpicRepository.findByProjectId(projectId).stream()
                .map(projectEpicMapper::toDto)
                .collect(toList());
    }

    public Optional<ProjectEpicDTO> getEpicById(Long id) {
        return projectEpicRepository.findById(id).map(projectEpicMapper::toDto);
    }

    @Transactional
    public ProjectEpicDTO save(ProjectEpicDTO projectEpicDTO) {
        Project project =
                projectRepository
                        .findById(projectEpicDTO.getProjectId())
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "Project not found: "
                                                        + projectEpicDTO.getProjectId()));

        ProjectEpic epic = projectEpicMapper.toEntity(projectEpicDTO);
        epic.setProject(project);

        ProjectEpic saved = projectEpicRepository.save(epic);

        return projectEpicMapper.toDto(saved);
    }

    @Transactional
    public ProjectEpicDTO updateEpic(Long id, ProjectEpicDTO updatedEpic) {
        return projectEpicRepository
                .findById(id)
                .map(
                        existingEpic -> {
                            existingEpic.setName(updatedEpic.getName());
                            existingEpic.setDescription(updatedEpic.getDescription());
                            existingEpic.setStartDate(updatedEpic.getStartDate());
                            existingEpic.setEndDate(updatedEpic.getEndDate());
                            return projectEpicRepository.save(existingEpic);
                        })
                .map(projectEpicMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Epic not found with id: " + id));
    }

    @Transactional
    public void deleteEpic(Long id) {
        projectEpicRepository.deleteById(id);
    }
}
