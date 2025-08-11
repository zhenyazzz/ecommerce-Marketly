package org.com.notificationservice.repository;

import org.com.notificationservice.model.Notification;
import org.com.notificationservice.model.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByUserIdAndStatus(Long userId, NotificationStatus status);

    List<Notification> findByRecipientAndStatus(String recipient, NotificationStatus status);

    Page<Notification> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT n FROM Notification n WHERE n.status = :status AND n.retryCount < :maxRetries")
    List<Notification> findPendingNotificationsForRetry(@Param("status") NotificationStatus status, 
                                                       @Param("maxRetries") Integer maxRetries);

    @Query("SELECT n FROM Notification n WHERE n.createdAt < :cutoffTime AND n.status = :status")
    List<Notification> findOldNotifications(@Param("cutoffTime") LocalDateTime cutoffTime, 
                                          @Param("status") NotificationStatus status);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userId = :userId AND n.type = :type AND n.createdAt >= :since")
    Long countNotificationsByUserAndTypeSince(@Param("userId") Long userId, 
                                             @Param("type") String type, 
                                             @Param("since") LocalDateTime since);
} 