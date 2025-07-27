package org.example.userservice.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.Instant;
import java.util.Set;

@Data
@AllArgsConstructor
public class UserProfileUpdatedEvent {
    private Long id;
    private String username;
    private String email;
    private Set<String> roles;
    private String status;
    private Instant updatedAt;
} 