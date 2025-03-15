package io.flowinquiry.modules.teams.service;

import static java.util.stream.Collectors.toList;

import io.flowinquiry.modules.teams.domain.ProjectEpic;
import io.flowinquiry.modules.teams.repository.ProjectEpicRepository;
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

    private final ProjectEpicRepository epicRepository;

    public List<ProjectEpicDTO> getAllEpics(Long projectId) {
        return epicRepository.findByProjectId(projectId).stream()
                .map(projectEpicMapper::toDto)
                .collect(toList());
    }

    public Optional<ProjectEpicDTO> getEpicById(Long id) {
        return epicRepository.findById(id).map(projectEpicMapper::toDto);
    }

    @Transactional
    public ProjectEpicDTO createEpic(ProjectEpic epic) {
        return projectEpicMapper.toDto(epicRepository.save(epic));
    }

    @Transactional
    public ProjectEpicDTO updateEpic(Long id, ProjectEpicDTO updatedEpic) {
        return epicRepository
                .findById(id)
                .map(
                        existingEpic -> {
                            existingEpic.setName(updatedEpic.getName());
                            existingEpic.setDescription(updatedEpic.getDescription());
                            existingEpic.setStartDate(updatedEpic.getStartDate());
                            existingEpic.setEndDate(updatedEpic.getEndDate());
                            return epicRepository.save(existingEpic);
                        })
                .map(projectEpicMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Epic not found with id: " + id));
    }

    @Transactional
    public void deleteEpic(Long id) {
        epicRepository.deleteById(id);
    }
}
