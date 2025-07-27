package org.com.orderservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEvent {
    private UUID userId;
    private String email;
    private String fullName;
    private UserEventType eventType;
    private LocalDateTime timestamp;
    
    public enum UserEventType {
        CREATED,
        UPDATED,
        DELETED,
        DEACTIVATED,
        ACTIVATED
    }
} 