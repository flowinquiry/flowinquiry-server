package io.flexwork.modules.usermanagement.repository;

import io.flexwork.modules.usermanagement.domain.AuthorityResourcePermission;
import io.flexwork.modules.usermanagement.domain.AuthorityResourcePermissionId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuthorityResourcePermissionRepository
        extends JpaRepository<AuthorityResourcePermission, AuthorityResourcePermissionId> {

    @Query(
            "SELECT new io.flexwork.modules.usermanagement.domain.AuthorityResourcePermission( "
                    + "    :authorityName, "
                    + "    r.name, "
                    + "    COALESCE(CAST(arp.permission AS integer), 0) "
                    + ") "
                    + "FROM Resource r "
                    + "LEFT JOIN AuthorityResourcePermission arp "
                    + "ON r.name = arp.resourceName "
                    + "AND arp.authorityName = :authorityName")
    List<AuthorityResourcePermission> findAllByAuthorityName(
            @Param("authorityName") String authorityName);
}
