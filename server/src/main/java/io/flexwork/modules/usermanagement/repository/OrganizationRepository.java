package io.flexwork.modules.usermanagement.repository;

import io.flexwork.modules.usermanagement.domain.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {}
