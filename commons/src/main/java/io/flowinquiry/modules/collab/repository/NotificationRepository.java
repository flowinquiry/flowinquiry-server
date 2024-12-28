package io.flowinquiry.modules.collab.repository;

import io.flowinquiry.modules.collab.domain.Notification;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdAndIsReadFalse(Long userId, Sort sort);

    Page<Notification> findByUserId(Long userId, Pageable pageable);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id IN :ids")
    void markAsRead(@Param("ids") List<Long> ids);
}
