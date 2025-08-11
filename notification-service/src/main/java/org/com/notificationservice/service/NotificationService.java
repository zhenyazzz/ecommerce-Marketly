package org.com.notificationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.notificationservice.dto.NotificationRequest;
import org.com.notificationservice.dto.OrderEvent;
import org.com.notificationservice.dto.UserEvent;
import org.com.notificationservice.model.Notification;
import org.com.notificationservice.model.NotificationChannel;
import org.com.notificationservice.model.NotificationStatus;
import org.com.notificationservice.model.NotificationType;
import org.com.notificationservice.repository.NotificationRepository;
import org.com.notificationservice.service.channel.NotificationChannelProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final TemplateService templateService;
    private final List<NotificationChannelProvider> channelProviders;

    @Transactional
    public Notification createNotification(NotificationRequest request) {
        log.info("Creating notification: type={}, channel={}, recipient={}", 
                request.getType(), request.getChannel(), request.getRecipient());

        Notification notification = Notification.builder()
                .type(request.getType())
                .channel(request.getChannel())
                .status(NotificationStatus.PENDING)
                .recipient(request.getRecipient())
                .subject(request.getSubject() != null ? request.getSubject() : 
                        templateService.getDefaultSubject(request.getType().name()))
                .content(request.getContent())
                .templateName(request.getTemplateName())
                .metadata(request.getMetadata())
                .userId(request.getUserId())
                .build();

        return notificationRepository.save(notification);
    }

    @Transactional
    public boolean sendNotification(Notification notification) {
        log.info("Sending notification: id={}, type={}, channel={}", 
                notification.getId(), notification.getType(), notification.getChannel());

        Optional<NotificationChannelProvider> provider = channelProviders.stream()
                .filter(p -> p.getChannelType().equals(notification.getChannel().name()))
                .findFirst();

        if (provider.isEmpty()) {
            log.error("No provider found for channel: {}", notification.getChannel());
            updateNotificationStatus(notification, NotificationStatus.FAILED, "No provider available");
            return false;
        }

        NotificationChannelProvider channelProvider = provider.get();
        
        if (!channelProvider.isAvailable()) {
            log.error("Channel provider is not available: {}", notification.getChannel());
            updateNotificationStatus(notification, NotificationStatus.FAILED, "Provider not available");
            return false;
        }

        try {
            boolean success = channelProvider.send(notification);
            
            if (success) {
                updateNotificationStatus(notification, NotificationStatus.SENT, null);
                log.info("Notification sent successfully: {}", notification.getId());
                return true;
            } else {
                updateNotificationStatus(notification, NotificationStatus.FAILED, "Provider returned false");
                return false;
            }
        } catch (Exception e) {
            log.error("Error sending notification: {}", notification.getId(), e);
            updateNotificationStatus(notification, NotificationStatus.FAILED, e.getMessage());
            return false;
        }
    }

    @Transactional
    public void processOrderEvent(OrderEvent event) {
        log.info("Processing order event: orderId={}, userId={}", event.getOrderId(), event.getUserId());

        NotificationType notificationType = determineOrderNotificationType(event);
        
        NotificationRequest request = NotificationRequest.builder()
                .type(notificationType)
                .channel(NotificationChannel.EMAIL)
                .recipient(event.getUserEmail())
                .templateName(getOrderTemplateName(notificationType))
                .templateData(Map.of(
                        "orderId", event.getOrderId().toString(),
                        "orderNumber", event.getOrderNumber(),
                        "userName", event.getUserName(),
                        "totalAmount", event.getTotalAmount().toString(),
                        "orderStatus", event.getOrderStatus(),
                        "previousStatus", event.getPreviousStatus(),
                        "newStatus", event.getNewStatus()
                ))
                .userId(event.getUserId())
                .build();

        Notification notification = createNotification(request);
        sendNotification(notification);
    }

    @Transactional
    public void processUserEvent(UserEvent event) {
        log.info("Processing user event: userId={}, eventType={}", event.getUserId(), event.getEventType());

        NotificationType notificationType = determineUserNotificationType(event);
        
        NotificationRequest request = NotificationRequest.builder()
                .type(notificationType)
                .channel(NotificationChannel.EMAIL)
                .recipient(event.getUserEmail())
                .templateName(getUserTemplateName(notificationType))
                .templateData(Map.of(
                        "userName", event.getUserName(),
                        "verificationToken", event.getVerificationToken(),
                        "resetToken", event.getResetToken(),
                        "reason", event.getReason()
                ))
                .userId(event.getUserId())
                .build();

        Notification notification = createNotification(request);
        sendNotification(notification);
    }

    private NotificationType determineOrderNotificationType(OrderEvent event) {
        if (event.getNewStatus() != null && event.getPreviousStatus() != null) {
            return NotificationType.ORDER_STATUS_UPDATED;
        }
        return NotificationType.ORDER_CREATED;
    }

    private NotificationType determineUserNotificationType(UserEvent event) {
        return switch (event.getEventType()) {
            case "USER_REGISTERED" -> NotificationType.USER_REGISTERED;
            case "EMAIL_VERIFIED" -> NotificationType.USER_EMAIL_VERIFIED;
            case "PASSWORD_RESET" -> NotificationType.PASSWORD_RESET;
            case "ACCOUNT_LOCKED" -> NotificationType.ACCOUNT_LOCKED;
            default -> NotificationType.USER_REGISTERED;
        };
    }

    private String getOrderTemplateName(NotificationType type) {
        return switch (type) {
            case ORDER_CREATED -> "order-created";
            case ORDER_STATUS_UPDATED -> "order-status-updated";
            case ORDER_CANCELLED -> "order-cancelled";
            default -> "order-notification";
        };
    }

    private String getUserTemplateName(NotificationType type) {
        return switch (type) {
            case USER_REGISTERED -> "user-registered";
            case USER_EMAIL_VERIFIED -> "email-verified";
            case PASSWORD_RESET -> "password-reset";
            case ACCOUNT_LOCKED -> "account-locked";
            default -> "user-notification";
        };
    }

    private void updateNotificationStatus(Notification notification, NotificationStatus status, String errorMessage) {
        notification.setStatus(status);
        notification.setErrorMessage(errorMessage);
        
        if (status == NotificationStatus.SENT) {
            notification.setSentAt(LocalDateTime.now());
        }
        
        if (status == NotificationStatus.FAILED) {
            notification.setRetryCount(notification.getRetryCount() + 1);
        }
        
        notificationRepository.save(notification);
    }
} 