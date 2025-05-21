package io.flowinquiry.modules.teams.repository;

import io.flowinquiry.modules.teams.domain.ProjectSetting;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectSettingRepository extends JpaRepository<ProjectSetting, Long> {

    Optional<ProjectSetting> findByProjectId(Long projectId);
}
