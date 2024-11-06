package io.flexwork.modules.crm.repository;

import io.flexwork.modules.crm.domain.Contact;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository
        extends JpaRepository<Contact, Long>, JpaSpecificationExecutor<Contact> {

    Page<Contact> findByAccountId(Long accountId, Pageable pageable);

    @Query("SELECT e FROM Contact e WHERE e.id > :currentId ORDER BY e.id ASC LIMIT 1")
    Optional<Contact> findNextEntity(@Param("currentId") Long currentId);

    @Query("SELECT e FROM Contact e WHERE e.id < :currentId ORDER BY e.id DESC LIMIT 1")
    Optional<Contact> findPreviousEntity(@Param("currentId") Long currentId);
}
