package org.com.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.com.notificationservice.model.NotificationChannel;
import org.com.notificationservice.model.NotificationType;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {

    @NotNull(message = "Notification type is required")
    private NotificationType type;

    @NotNull(message = "Channel is required")
    private NotificationChannel channel;

    @NotBlank(message = "Recipient is required")
    @Email(message = "Invalid email format")
    private String recipient;

    private String subject;

    private String content;

    private String templateName;

    private Map<String, String> templateData;

    private Long userId;

    private Map<String, String> metadata;
} 