package io.flexwork.modules.usermanagement.repository;

import io.flexwork.modules.usermanagement.domain.Tenant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
    Optional<Tenant> findByNameIgnoreCase(String name);

    Optional<Tenant> findByDomainContainingIgnoreCase(String domain);
}