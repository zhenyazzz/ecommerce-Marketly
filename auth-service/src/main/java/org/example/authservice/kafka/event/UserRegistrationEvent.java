package org.example.authservice.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class UserRegistrationEvent {
    private String username;
    private Set<String> roles;
}
