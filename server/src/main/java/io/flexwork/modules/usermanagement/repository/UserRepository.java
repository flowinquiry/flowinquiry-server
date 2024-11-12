package io.flexwork.modules.usermanagement.repository;

import io.flexwork.modules.usermanagement.domain.User;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** Spring Data JPA repository for the {@link User} entity. */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    String USERS_BY_EMAIL_CACHE = "usersByEmail";

    Optional<User> findOneByActivationKey(String activationKey);

    List<User> findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(
            Instant dateTime);

    Optional<User> findOneByResetKey(String resetKey);

    Optional<User> findOneByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = "authorities")
    @Cacheable(cacheNames = USERS_BY_EMAIL_CACHE)
    Optional<User> findOneWithAuthoritiesByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesById(Long id);

    Page<User> findAllByIdNotNullAndActivatedIsTrue(Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.lastLoginTime = :lastLoginTime WHERE u.email = :userEmail")
    void updateLastLoginTime(String userEmail, LocalDateTime lastLoginTime);

    @Query("SELECT u FROM User u JOIN u.teams t WHERE t.id = :teamId")
    Page<User> findAllByTeamId(@Param("teamId") Long teamId, Pageable pageable);

    @Query("SELECT u FROM User u JOIN u.authorities a WHERE a.name = :authorityName")
    List<User> findAllUsersByAuthority(@Param("authorityName") String authorityName);

    @Query(
            "SELECT u FROM User u "
                    + "WHERE (LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) "
                    + "OR LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) "
                    + "AND :authorityName NOT IN (SELECT a.name FROM u.authorities a)")
    List<User> findUsersNotInAuthority(
            @Param("searchTerm") String searchTerm,
            @Param("authorityName") String authorityName,
            Pageable pageable);
}
