package org.example.userservice.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.Instant;

@Data
@AllArgsConstructor
public class UserDeletedEvent {
    private Long id;
    private String username;
    private String email;
    private Instant deletedAt;
} 