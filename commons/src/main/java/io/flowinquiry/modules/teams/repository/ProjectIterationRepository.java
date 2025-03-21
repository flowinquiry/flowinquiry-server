package io.flowinquiry.modules.teams.repository;

import io.flowinquiry.modules.teams.domain.ProjectIteration;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectIterationRepository
        extends JpaRepository<ProjectIteration, Long>, JpaSpecificationExecutor<ProjectIteration> {

    List<ProjectIteration> findByProjectIdOrderByStartDateAsc(Long projectId);
}
