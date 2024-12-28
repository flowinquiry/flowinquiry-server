package io.flowinquiry.modules.teams.repository;

import io.flowinquiry.modules.teams.domain.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository
        extends JpaRepository<Organization, Long>, JpaSpecificationExecutor<Organization> {}
