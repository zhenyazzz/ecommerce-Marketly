package org.com.notificationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.notificationservice.model.Notification;
import org.com.notificationservice.model.NotificationStatus;
import org.com.notificationservice.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetryService {

    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    @Value("${notification.retry.max-attempts:3}")
    private Integer maxRetryAttempts;

    @Value("${notification.retry.delay-minutes:5}")
    private Integer retryDelayMinutes;

    /**
     * Retry failed notifications every 5 minutes
     */
    @Scheduled(fixedRate = 300000) // 5 minutes
    public void retryFailedNotifications() {
        log.info("Starting retry process for failed notifications");
        
        List<Notification> failedNotifications = notificationRepository
                .findPendingNotificationsForRetry(NotificationStatus.FAILED, maxRetryAttempts);

        log.info("Found {} failed notifications to retry", failedNotifications.size());

        for (Notification notification : failedNotifications) {
            try {
                log.info("Retrying notification: id={}, retryCount={}", 
                        notification.getId(), notification.getRetryCount());
                
                boolean success = notificationService.sendNotification(notification);
                
                if (success) {
                    log.info("Successfully retried notification: {}", notification.getId());
                } else {
                    log.warn("Failed to retry notification: {}", notification.getId());
                }
            } catch (Exception e) {
                log.error("Error retrying notification: {}", notification.getId(), e);
            }
        }
    }

    /**
     * Clean up old notifications (older than 30 days)
     */
    @Scheduled(cron = "0 0 2 * * ?") // Daily at 2 AM
    public void cleanupOldNotifications() {
        log.info("Starting cleanup of old notifications");
        
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
        List<Notification> oldNotifications = notificationRepository
                .findOldNotifications(cutoffDate, NotificationStatus.SENT);

        log.info("Found {} old notifications to cleanup", oldNotifications.size());

        for (Notification notification : oldNotifications) {
            try {
                notificationRepository.delete(notification);
                log.debug("Deleted old notification: {}", notification.getId());
            } catch (Exception e) {
                log.error("Error deleting old notification: {}", notification.getId(), e);
            }
        }
    }

    /**
     * Manual retry for specific notification
     */
    public boolean retryNotification(String notificationId) {
        try {
            Notification notification = notificationRepository.findById(java.util.UUID.fromString(notificationId))
                    .orElseThrow(() -> new RuntimeException("Notification not found"));

            if (notification.getStatus() != NotificationStatus.FAILED) {
                log.warn("Notification {} is not in FAILED status", notificationId);
                return false;
            }

            if (notification.getRetryCount() >= maxRetryAttempts) {
                log.warn("Notification {} has exceeded max retry attempts", notificationId);
                return false;
            }

            return notificationService.sendNotification(notification);
        } catch (Exception e) {
            log.error("Error retrying notification: {}", notificationId, e);
            return false;
        }
    }
} 