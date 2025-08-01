package org.example.authservice.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserLoginEvent {
    private String username;
    private List<String> roles;
}
