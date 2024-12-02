package io.flexwork.modules.teams.repository;

import io.flexwork.modules.teams.domain.Team;
import io.flexwork.modules.teams.service.dto.TeamDTO;
import io.flexwork.modules.usermanagement.domain.User;
import io.flexwork.modules.usermanagement.service.dto.UserWithTeamRoleDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long>, JpaSpecificationExecutor<Team> {
    @Query(
            "SELECT new io.flexwork.modules.teams.service.dto.TeamDTO(t.id, t.name, t.logoUrl, t.slogan, t.description, t.organization.id, COUNT(m.id)) "
                    + "FROM Team t LEFT JOIN t.users m "
                    + "GROUP BY t.id")
    Page<TeamDTO> findTeams(Specification<Team> spec, Pageable pageable);

    @Query("SELECT t FROM Team t JOIN t.users u WHERE u.id = :userId")
    List<Team> findTeamsByUserId(@Param("userId") Long userId);

    @Query(
            """
        SELECT u
        FROM User u
        WHERE (LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
           OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
           OR LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
          AND u.id NOT IN (
              SELECT ut.id
              FROM Team t
              JOIN t.users ut
              WHERE t.id = :teamId
          )
    """)
    List<User> findUsersNotInTeam(
            @Param("searchTerm") String searchTerm,
            @Param("teamId") Long teamId,
            Pageable pageable);

    @Query(
            "SELECT new io.flexwork.modules.usermanagement.service.dto.UserWithTeamRoleDTO(u.id, u.email, u.firstName, u.lastName, u.timezone, u.imageUrl, u.title, ut.team.id, ut.role.name) "
                    + "FROM User u JOIN u.userTeams ut WHERE ut.team.id = :teamId")
    List<UserWithTeamRoleDTO> findUsersByTeamId(@Param("teamId") Long teamId);

    /**
     * Return the team role of user, return default value is 'Guest'
     *
     * @param userId
     * @param teamId
     * @return
     */
    @Query(
            """
           SELECT COALESCE(ut.role.name, 'Guest')
           FROM UserTeam ut
           WHERE ut.user.id = :userId
             AND ut.team.id = :teamId
           """)
    String findUserRoleInTeam(@Param("userId") Long userId, @Param("teamId") Long teamId);
}
