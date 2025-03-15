package io.flowinquiry.modules.teams.repository;

import io.flowinquiry.modules.teams.domain.ProjectEpic;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectEpicRepository
        extends JpaRepository<ProjectEpic, Long>, JpaSpecificationExecutor<ProjectEpic> {

    List<ProjectEpic> findByProjectId(Long projectId);
}
