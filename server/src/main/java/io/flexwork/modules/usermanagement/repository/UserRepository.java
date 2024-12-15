package io.flexwork.modules.usermanagement.repository;

import io.flexwork.modules.usermanagement.domain.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/** Spring Data JPA repository for the {@link User} entity. */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    String USERS_BY_EMAIL_CACHE = "usersByEmail";

    Optional<User> findOneByActivationKey(String activationKey);

    Optional<User> findOneByResetKey(String resetKey);

    Optional<User> findOneByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = "authorities")
    @Cacheable(cacheNames = USERS_BY_EMAIL_CACHE)
    Optional<User> findOneWithAuthoritiesByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = {"manager"})
    Optional<User> findOneWithManagerById(Long id);

    /**
     * Finds all direct reports of a specific user by their manager ID.
     *
     * @param managerId the ID of the manager.
     * @return a list of direct reports.
     */
    List<User> findByManagerId(Long managerId);

    Optional<User> findUserByEmailEqualsIgnoreCase(String email);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.lastLoginTime = :lastLoginTime WHERE u.email = :userEmail")
    void updateLastLoginTime(String userEmail, LocalDateTime lastLoginTime);

    @Query(
            value =
                    """
    SELECT r.name AS resourceName,
           CASE
               WHEN EXISTS (
                   SELECT 1
                   FROM fw_user_authority uaAdmin
                   WHERE uaAdmin.user_id = :userId
                   AND uaAdmin.authority_name = 'ROLE_ADMIN'
               ) THEN 'ACCESS'
               ELSE
                   CASE MAX(
                       CASE rp.permission::int
                           WHEN 0 THEN 0  -- NONE
                           WHEN 1 THEN 1  -- READ
                           WHEN 2 THEN 2  -- WRITE
                           WHEN 3 THEN 3  -- ACCESS
                       END
                   )
                   WHEN 0 THEN 'NONE'
                   WHEN 1 THEN 'READ'
                   WHEN 2 THEN 'WRITE'
                   WHEN 3 THEN 'ACCESS'
                   END
           END AS permission
    FROM fw_resource r
    LEFT JOIN fw_authority_resource_permission rp ON rp.resource_name = r.name
    LEFT JOIN fw_user_authority ua ON rp.authority_name = ua.authority_name AND ua.user_id = :userId
    GROUP BY r.name
    """,
            nativeQuery = true)
    List<Object[]> findResourcesWithHighestPermissionsByUserId(@Param("userId") Long userId);
}
