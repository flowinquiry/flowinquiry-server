package io.flexwork.modules.usermanagement.repository;

import io.flexwork.modules.usermanagement.domain.AuthorityResourcePermission;
import io.flexwork.modules.usermanagement.domain.AuthorityResourcePermissionId;
import io.flexwork.modules.usermanagement.domain.Permission;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuthorityResourcePermissionRepository
        extends JpaRepository<AuthorityResourcePermission, AuthorityResourcePermissionId> {

    Optional<AuthorityResourcePermission> findByAuthorityNameAndResourceNameAndPermission(
            String authority, String resourceName, Permission permission);

    @Query(
            "SELECT new io.flexwork.modules.usermanagement.domain.AuthorityResourcePermission( "
                    + "    :authorityName, "
                    + "    r.name, "
                    + "    COALESCE(arp.permission, 'NONE')"
                    + ") "
                    + "FROM Resource r "
                    + "LEFT JOIN AuthorityResourcePermission arp "
                    + "ON r.name = arp.resourceName "
                    + "AND arp.authorityName = :authorityName")
    List<AuthorityResourcePermission> findAllByAuthorityName(
            @Param("authorityName") String authorityName);
}
