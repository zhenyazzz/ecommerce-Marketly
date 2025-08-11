package org.com.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent {
    private Long userId;
    private String userEmail;
    private String userName;
    private String eventType;
    private LocalDateTime eventDate;
    private String verificationToken;
    private String resetToken;
    private String reason;
} 