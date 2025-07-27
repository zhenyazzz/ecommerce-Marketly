package org.example.authservice.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
public class UserRegistrationEvent {
    private Long userId;
    private String username;
    private Set<String> roles;
}
