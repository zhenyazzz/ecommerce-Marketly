package org.example.userservice.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRoleUpdateEvent {
    private String username;
    private String role;
    private String action; // Например, "ADDED" или "REMOVED"
} 