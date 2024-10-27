package io.flexwork.modules.usermanagement.repository;

import io.flexwork.modules.usermanagement.domain.Authority;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Spring Data JPA repository for the Authority entity. */
@SuppressWarnings("unused")
@Repository
public interface AuthorityRepository extends JpaRepository<Authority, String> {
    List<Authority> findByNameIn(List<String> authorityNames);

    Optional<Authority> findByDescriptiveName(String descriptiveName);
}
