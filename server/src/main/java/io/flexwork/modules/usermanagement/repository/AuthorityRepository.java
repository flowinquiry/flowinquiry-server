package io.flexwork.modules.usermanagement.repository;

import io.flexwork.modules.usermanagement.domain.Authority;
import io.flexwork.modules.usermanagement.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** Spring Data JPA repository for the Authority entity. */
@SuppressWarnings("unused")
@Repository
public interface AuthorityRepository extends JpaRepository<Authority, String> {
    List<Authority> findByNameIn(List<String> authorityNames);

    Optional<Authority> findByName(String name);

    Optional<Authority> findByDescriptiveName(String descriptiveName);

    @Query(
            "SELECT u FROM User u "
                    + "WHERE (LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) "
                    + "OR LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) "
                    + "AND :authorityName NOT IN (SELECT a.name FROM u.authorities a)")
    List<User> findUsersNotInAuthority(
            @Param("searchTerm") String searchTerm,
            @Param("authorityName") String authorityName,
            Pageable pageable);

    @Query("SELECT u FROM User u JOIN u.authorities a WHERE a.name = :authorityName")
    Page<User> findUsersByAuthority(
            @Param("authorityName") String authorityName, Pageable pageable);
}
