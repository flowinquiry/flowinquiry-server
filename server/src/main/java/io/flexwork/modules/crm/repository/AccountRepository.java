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

    // Find an account by its name
    Optional<Account> findByName(String accountName);

    // Find accounts by their type
    List<Account> findByType(String accountType);

    // Find accounts by their status
    List<Account> findByStatus(String status);

    // Find accounts by industry
    List<Account> findByIndustry(String industry);

    // Find accounts by parent account
    List<Account> findByParentAccount(Account parentAccount);

    // Find accounts assigned to a specific user
    List<Account> findByAssignedToUserId(Long assignedToUserId);

    // Query to find the next entity based on the current primary key
    @Query("SELECT e FROM Account e WHERE e.id > :currentId ORDER BY e.id ASC LIMIT 1")
    Optional<Account> findNextEntity(@Param("currentId") Long currentId);

    // Query to find the previous entity based on the current primary key
    @Query("SELECT e FROM Account e WHERE e.id < :currentId ORDER BY e.id DESC LIMIT 1")
    Optional<Account> findPreviousEntity(@Param("currentId") Long currentId);
}
