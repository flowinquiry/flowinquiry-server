package io.flexwork.modules.crm.repository;

import io.flexwork.modules.crm.domain.Account;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository
        extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {

    Optional<Account> findByName(String accountName);

    List<Account> findByType(String accountType);

    List<Account> findByStatus(String status);

    List<Account> findByIndustry(String industry);

    List<Account> findByParentAccount(Account parentAccount);

    List<Account> findByAssignedToUserId(Long assignedToUserId);

    @Query("SELECT e FROM Account e WHERE e.id > :currentId ORDER BY e.id ASC LIMIT 1")
    Optional<Account> findNextEntity(@Param("currentId") Long currentId);

    @Query("SELECT e FROM Account e WHERE e.id < :currentId ORDER BY e.id DESC LIMIT 1")
    Optional<Account> findPreviousEntity(@Param("currentId") Long currentId);
}
